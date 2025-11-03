package thanhcom.site.lkdt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thanhcom.site.lkdt.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role , Long> {
    Role findByName(String name);
}
