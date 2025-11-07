package thanhcom.site.lkdt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import thanhcom.site.lkdt.entity.Role;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role , Long> {
    Optional<Role> findByName(String name);

    List<Role> findByAccountId(Long accountId);
}
