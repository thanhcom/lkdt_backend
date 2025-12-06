package thanhcom.site.lkdt.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thanhcom.site.lkdt.entity.Component;
import thanhcom.site.lkdt.entity.OrderItem;
import thanhcom.site.lkdt.entity.Orders;
import thanhcom.site.lkdt.enums.ErrCode;
import thanhcom.site.lkdt.exception.AppException;
import thanhcom.site.lkdt.repository.ComponentRepository;
import thanhcom.site.lkdt.repository.CustomerRepository;
import thanhcom.site.lkdt.repository.OrdersRepository;
import thanhcom.site.lkdt.specification.OrdersSpecification;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrdersService {
    OrdersRepository ordersRepository;
    CustomerRepository customerRepository;
    ComponentRepository componentRepository;
    OrderHistoryService historyService;

    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }

    public Page<Orders> searchOrders(
            Long orderId,
            Long customerId,
            BigDecimal minTotal,
            BigDecimal maxTotal,
            OffsetDateTime dateFrom,
            OffsetDateTime dateTo,
            String status,
            String keyword,
            Pageable pageable
    ) {
        Specification<Orders> spec = OrdersSpecification.hasOrderId(orderId)
                .and(OrdersSpecification.hasCustomer(customerId))
                .and(OrdersSpecification.hasMinTotal(minTotal))
                .and(OrdersSpecification.hasMaxTotal(maxTotal))
                .and(OrdersSpecification.dateFrom(dateFrom))
                .and(OrdersSpecification.dateTo(dateTo))
                .and(OrdersSpecification.hasStatus(status))
                .and(OrdersSpecification.keyword(keyword));

        return ordersRepository.findAll(spec, pageable);
    }


    @Transactional
    public Orders createOrder(Orders order) {
        // Validate customer
        if (order.getCustomer() == null || order.getCustomer().getId() == null) {
            throw new IllegalArgumentException("Customer không được null");
        }

        // Optional: fetch full customer entity nếu FE chỉ gửi ID
        var customer = customerRepository.findById(order.getCustomer().getId())
                .orElseThrow(() -> new IllegalArgumentException("Customer không tồn tại"));
        order.setCustomer(customer);

        // Validate items
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new IllegalArgumentException("Đơn hàng phải có ít nhất 1 sản phẩm");
        }

        // Set quan hệ 2 chiều và tính total của từng item
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            Component c = componentRepository.findById(item.getComponent().getId())
                    .orElseThrow(() -> new AppException(ErrCode.COMPONENT_NOTFOUND));
            // kiểm tra tồn kho
            if (c.getStockQuantity() < item.getQuantity()) {
                throw new AppException(ErrCode.ORDER_INSUFFICIENT_STOCK);
            }
            // trừ số lượng
            c.setStockQuantity(c.getStockQuantity()- item.getQuantity());
            componentRepository.save(c);

            item.setOrder(order); // set quan hệ 2 chiều
            if (item.getQuantity() == null || item.getPrice() == null) {
                throw new IllegalArgumentException("Số lượng và giá sản phẩm không được null");
            }
            item.setTotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            totalAmount = totalAmount.add(item.getTotal());
        }

        // Set tổng tiền
        order.setTotalAmount(totalAmount);

        // Set trạng thái mặc định nếu null
        if (order.getStatus() == null) {
            order.setStatus("PENDING");
        }

        // Lưu đơn hàng cùng items
        Orders saved = ordersRepository.save(order);
        // Ghi log tạo đơn
        historyService.log(saved, "CREATED",
                "Tạo đơn hàng mới. Tổng tiền: " + saved.getTotalAmount());

        return saved;

    }

    public Orders getOrderById(Long id) {
        return ordersRepository.findById(id).orElse(null);
    }

    @Transactional
    public Orders updateOrder(Long id, Orders updatedOrder) {
        Orders order = ordersRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrCode.ORDER_NOT_FOUND));
        // update status
        order.setStatus(updatedOrder.getStatus());
        // ===========================================
        // 1. Map item cũ theo componentId
        // ===========================================
        Map<Long, OrderItem> oldItems = order.getItems()
                .stream()
                .collect(Collectors.toMap(i -> i.getComponent().getId(), i -> i));
        List<OrderItem> finalItems = new ArrayList<>();
        // ===========================================
        // 2. Xử lý từng item mới
        // ===========================================
        for (OrderItem newItem : updatedOrder.getItems()) {
            Long compId = newItem.getComponent().getId();
            Component comp = componentRepository.findById(compId)
                    .orElseThrow(() -> new AppException(ErrCode.COMPONENT_NOTFOUND));
            OrderItem oldItem = oldItems.get(compId);
            if (oldItem == null) {
                // ---------------------------------------
                // 🆕 Item mới → Trừ kho theo quantity mới
                // ---------------------------------------
                if (comp.getStockQuantity() < newItem.getQuantity()) {
                    throw new AppException(ErrCode.ORDER_INSUFFICIENT_STOCK);
                }
                comp.setStockQuantity(comp.getStockQuantity() - newItem.getQuantity());
                componentRepository.save(comp);
                newItem.setOrder(order);
                // total do DB auto generate vì bạn đặt insertable = false
                finalItems.add(newItem);
            } else {
                // ---------------------------------------
                // 🔁 Item cũ → So sánh qty cũ và mới
                // ---------------------------------------
                int oldQty = oldItem.getQuantity();
                int newQty = newItem.getQuantity();
                if (newQty > oldQty) {
                    // cần trừ thêm kho
                    int diff = newQty - oldQty;
                    if (comp.getStockQuantity() < diff) {
                        throw new AppException(ErrCode.ORDER_INSUFFICIENT_STOCK);
                    }
                    comp.setStockQuantity(comp.getStockQuantity() - diff);
                } else if (newQty < oldQty) {
                    // cần hoàn kho
                    int diff = oldQty - newQty;
                    comp.setStockQuantity(comp.getStockQuantity() + diff);
                }
                componentRepository.save(comp);
                // cập nhật item cũ
                oldItem.setQuantity(newQty);
                oldItem.setPrice(newItem.getPrice());
                finalItems.add(oldItem);
                // đánh dấu đã xử lý
                oldItems.remove(compId);
            }
        }
        // ===========================================
        // 3. Item bị xóa hoàn toàn → trả lại kho
        // ===========================================
        for (OrderItem removed : oldItems.values()) {
            Component comp = componentRepository.findById(removed.getComponent().getId())
                    .orElseThrow(() -> new AppException(ErrCode.COMPONENT_NOTFOUND));

            comp.setStockQuantity(comp.getStockQuantity() + removed.getQuantity());
            componentRepository.save(comp);
        }
        // ===========================================
        // 4. Gán lại items (JPA tự orphanRemoval)
        // ===========================================
        order.getItems().clear();
        order.getItems().addAll(finalItems);
        // ===========================================
        // 5. Tính lại totalAmount (total do DB auto)
        // ===========================================
        BigDecimal total = order.getItems().stream()
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        order.setTotalAmount(total);
        Orders updated = ordersRepository.save(order);

// Ghi log trạng thái
        historyService.log(updated, "UPDATED",
                "Cập nhật trạng thái thành: " + updatedOrder.getStatus());

// Log chi tiết item
        StringBuilder detail = new StringBuilder();
        for (OrderItem newItem : updatedOrder.getItems()) {
            detail.append(
                    String.format("Item #%d: %d cái, giá %s\n",
                            newItem.getComponent().getId(),
                            newItem.getQuantity(),
                            newItem.getPrice())
            );
        }

        historyService.log(updated, "UPDATED_ITEMS",
                "Danh sách item sau cập nhật:\n" + detail);

        return updated;

    }


    @Transactional
    public void deleteOrder(Long id) {
        Orders order = ordersRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrCode.ORDER_NOT_FOUND));
        // =============================
        // 1. Hoàn lại tồn kho
        // =============================
        for (OrderItem item : order.getItems()) {
            Component comp = componentRepository.findById(item.getComponent().getId())
                    .orElseThrow(() -> new AppException(ErrCode.COMPONENT_NOTFOUND));
            comp.setStockQuantity(comp.getStockQuantity() + item.getQuantity());
            componentRepository.save(comp);
        }
        // =============================
        // 2. Xóa order (JPA tự xóa item nhờ orphanRemoval)
        // =============================
        historyService.log(order, "DELETED",
                "Xóa đơn hàng và hoàn lại tồn kho");

        ordersRepository.delete(order);
    }

}
