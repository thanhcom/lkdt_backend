package thanhcom.site.lkdt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import thanhcom.site.lkdt.entity.Permission;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission , Long> {

    @Query("""
    SELECT DISTINCT p
    FROM Permission p
    JOIN p.roles r
    JOIN r.account a
    WHERE a.id = :userId
""")
    List<Permission> getPermissionsByUser(@Param("userId") Long userId);
}
