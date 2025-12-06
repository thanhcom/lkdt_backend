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
            Component c = componentRepository.findById(item.getId())
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
        return ordersRepository.save(order);
    }

    public Orders getOrderById(Long id) {
        return ordersRepository.findById(id).orElse(null);
    }

    @Transactional
    public Orders updateOrder(Long id, Orders updatedOrder) {
        return ordersRepository.findById(id).map(order -> {
            order.setStatus(updatedOrder.getStatus());

            if (updatedOrder.getItems() != null) {
                Map<Long, OrderItem> existingItems = order.getItems().stream()
                        .collect(Collectors.toMap(i -> i.getComponent().getId(), i -> i));

                List<OrderItem> newItems = new ArrayList<>();
                for (OrderItem updatedItem : updatedOrder.getItems()) {
                    OrderItem item = existingItems.getOrDefault(
                            updatedItem.getComponent().getId(),
                            new OrderItem()
                    );
                    item.setOrder(order);
                    item.setComponent(updatedItem.getComponent());
                    item.setQuantity(updatedItem.getQuantity());
                    item.setPrice(updatedItem.getPrice());
                    newItems.add(item);
                }
                order.getItems().clear();
                order.getItems().addAll(newItems);
            }

            // Tính lại totalAmount
            BigDecimal total = order.getItems().stream()
                    .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            order.setTotalAmount(total);

            return ordersRepository.save(order);
        }).orElse(null);
    }



    public void deleteOrder(Long id) {
        ordersRepository.findById(id).map(order -> {
            ordersRepository.delete(order);
            return true;
        });
    }
}
