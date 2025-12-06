package thanhcom.site.lkdt.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import thanhcom.site.lkdt.entity.OrderHistory;
import thanhcom.site.lkdt.entity.Orders;
import thanhcom.site.lkdt.repository.OrderHistoryRepository;
import thanhcom.site.lkdt.specification.OrderHistorySpecification;
import thanhcom.site.lkdt.utility.SecurityUtils;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OrderHistoryService {

    OrderHistoryRepository historyRepository;

    /**
     * Tạo log khi có action trên Orders
     */
    public void log(Orders order, String action, String description) {
        OrderHistory history = new OrderHistory();
        history.setOrder(order);
        history.setAction(action);
        history.setDescription(description);

        // Lấy username hiện tại
        String currentUser = SecurityUtils.getCurrentUsername();
        history.setUpdatedBy(currentUser);

        // Lưu log
        historyRepository.save(history);
    }

    /**
     * Search/filter OrderHistory theo nhiều tiêu chí
     */
    public Page<OrderHistory> searchOrderHistories(
            Long customerId,
            String customerName,
            Long componentId,
            String componentName,
            OffsetDateTime createdFrom,
            OffsetDateTime createdTo,
            String action,
            String updatedBy,
            Pageable pageable
    ) {
        return historyRepository.findAll(
                OrderHistorySpecification.filter(
                        customerId, customerName,
                        componentId, componentName,
                        createdFrom, createdTo,
                        action, updatedBy
                ),
                pageable
        );
    }

    /**
     * Lấy tất cả OrderHistory (không paging)
     */
    public List<OrderHistory> getAllOrderHistories() {
        return historyRepository.findAll();
    }

    /**
     * Lấy OrderHistory theo ID
     */
    public OrderHistory getOrderHistoryById(Long id) {
        return historyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("OrderHistory không tồn tại với id = " + id));
    }
}
