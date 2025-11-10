package thanhcom.site.lkdt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import thanhcom.site.lkdt.entity.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction , Long> {
    List<Transaction> findByComponentId(Long componentId);

    List<Transaction> findByProjectId(Long projectId);
}
