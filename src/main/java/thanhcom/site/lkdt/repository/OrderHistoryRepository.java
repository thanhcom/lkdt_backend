package thanhcom.site.lkdt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import thanhcom.site.lkdt.entity.OrderHistory;

import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long>, JpaSpecificationExecutor<OrderHistory> {

    // Lấy lịch sử theo Order
    List<OrderHistory> findByOrderIdOrderByCreatedAtDesc(Long orderId);
}
