package thanhcom.site.lkdt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import thanhcom.site.lkdt.entity.OrderHistory;

import java.util.List;

@Repository
public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Long>, JpaSpecificationExecutor<OrderHistory> {
    @EntityGraph(attributePaths = {"order.items", "order.items.component"})
    Page<OrderHistory> findAll(Specification<OrderHistory> spec, Pageable pageable);

    // Lấy lịch sử theo Order
    List<OrderHistory> findByOrderIdOrderByCreatedAtDesc(Long orderId);
}
