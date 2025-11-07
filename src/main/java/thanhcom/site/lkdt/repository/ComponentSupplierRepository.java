package thanhcom.site.lkdt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import thanhcom.site.lkdt.entity.ComponentSupplier;

import java.util.List;

public interface ComponentSupplierRepository extends JpaRepository<ComponentSupplier, Long> {
    @Modifying
    @Query("DELETE FROM ComponentSupplier cs WHERE cs.component.id = :componentId")
    void deleteByComponentId(@Param("componentId") Long componentId);

    List<ComponentSupplier> findAllByComponentId(Long componentId);

    List<ComponentSupplier> findAllBySupplierId(Long supplierId);
}
