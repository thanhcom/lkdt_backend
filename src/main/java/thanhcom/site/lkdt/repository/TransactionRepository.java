package thanhcom.site.lkdt.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import thanhcom.site.lkdt.entity.Transaction;
import thanhcom.site.lkdt.enums.TransactionType;

import java.time.OffsetDateTime;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction , Long> , JpaSpecificationExecutor<Transaction> {
    List<Transaction> findByComponentId(Long componentId);

    List<Transaction> findByProjectId(Long projectId);

    @Query(
            value = """
                SELECT t FROM Transaction t
                LEFT JOIN t.component c
                LEFT JOIN t.project p
                WHERE (:componentId IS NULL OR c.id = :componentId)
                  AND (:componentName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :componentName, '%')))
                  AND (:projectId IS NULL OR p.id = :projectId)
                  AND (:projectName IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :projectName, '%')))
                  AND (:type IS NULL OR t.transactionType = :type)
                  AND (
                        (:start IS NULL AND :end IS NULL)
                        OR (:start IS NOT NULL AND :end IS NOT NULL AND t.transactionDate BETWEEN :start AND :end)
                        OR (:start IS NOT NULL AND :end IS NULL AND t.transactionDate >= :start)
                        OR (:start IS NULL AND :end IS NOT NULL AND t.transactionDate <= :end)
                      )
                """,
            countQuery = """
                SELECT COUNT(t) FROM Transaction t
                LEFT JOIN t.component c
                LEFT JOIN t.project p
                WHERE (:componentId IS NULL OR c.id = :componentId)
                  AND (:componentName IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :componentName, '%')))
                  AND (:projectId IS NULL OR p.id = :projectId)
                  AND (:projectName IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :projectName, '%')))
                  AND (:type IS NULL OR t.transactionType = :type)
                  AND (
                        (:start IS NULL AND :end IS NULL)
                        OR (:start IS NOT NULL AND :end IS NOT NULL AND t.transactionDate BETWEEN :start AND :end)
                        OR (:start IS NOT NULL AND :end IS NULL AND t.transactionDate >= :start)
                        OR (:start IS NULL AND :end IS NOT NULL AND t.transactionDate <= :end)
                      )
            """
    )
    Page<Transaction> searchTransactions(
            @Param("componentId") Long componentId,
            @Param("componentName") String componentName,
            @Param("projectId") Long projectId,
            @Param("projectName") String projectName,
            @Param("type") TransactionType type,  // hoặc String nếu bạn muốn
            @Param("start") OffsetDateTime start,
            @Param("end") OffsetDateTime end,
            Pageable pageable
    );
}
