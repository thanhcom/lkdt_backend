package thanhcom.site.lkdt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import thanhcom.site.lkdt.entity.Project;

import java.util.List;

@Repository
public interface ProjectRepository extends JpaRepository<Project , Long> {
    @Query("""
    SELECT DISTINCT p
    FROM Project p
    JOIN p.transactions t
    WHERE t.id = :transactionId
""")
    List<Project> findByTransactionId(Long transactionId);

    @Query("""
    SELECT DISTINCT p
    FROM Project p
    JOIN p.transactions t
    JOIN t.component c
    WHERE c.id = :componentId
""")
    List<Project> findByComponentId(Long componentId);
}
