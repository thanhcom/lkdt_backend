package thanhcom.site.lkdt.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import thanhcom.site.lkdt.entity.Orders;

import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders> {
    @EntityGraph(attributePaths = {"items", "items.component"})
    Optional<Orders> findWithItemsAndComponentsById(Long id);
}
