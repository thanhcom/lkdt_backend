package thanhcom.site.lkdt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thanhcom.site.lkdt.entity.Supplier;
@Repository
public interface SupplierRepository extends JpaRepository<Supplier , Long> {
}
