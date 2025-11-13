package thanhcom.site.lkdt.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import thanhcom.site.lkdt.entity.Component;
import thanhcom.site.lkdt.entity.Project;
import thanhcom.site.lkdt.entity.Transaction;
import thanhcom.site.lkdt.enums.ErrCode;
import thanhcom.site.lkdt.exception.AppException;
import thanhcom.site.lkdt.repository.ComponentRepository;
import thanhcom.site.lkdt.repository.ProjectRepository;
import thanhcom.site.lkdt.repository.TransactionRepository;
import thanhcom.site.lkdt.specification.TransactionSpecification;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class TransactionService {
    TransactionRepository transactionRepository;
    ComponentRepository componentRepository;
    ProjectRepository projectRepository;
    @Transactional(readOnly = true)
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Page<Transaction> searchTransactions(
            Long componentId,
            Long projectId,
            String componentName,
            String projectName,
            String type,
            OffsetDateTime start,
            OffsetDateTime end,
            int page,
            int size
    ) {
        Specification<Transaction> spec = TransactionSpecification.hasComponentId(componentId)
                .and(TransactionSpecification.hasProjectId(projectId))
                .and(TransactionSpecification.hasComponentName(componentName))
                .and(TransactionSpecification.hasProjectName(projectName))
                .and(TransactionSpecification.hasTransactionType(type))
                .and(TransactionSpecification.betweenDates(start, end));

        return transactionRepository.findAll(spec, PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByComponentId(Long componentId) {
        if (!componentRepository.existsById(componentId)) {
            throw new AppException(ErrCode.COMPONENT_NOTFOUND);
        }
        return transactionRepository.findByComponentId(componentId);
    }

    @Transactional(readOnly = true)
    public List<Transaction> getTransactionsByProjectId(Long projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new AppException(ErrCode.PROJECT_NOT_FOUND);
        }
        return transactionRepository.findByProjectId(projectId);
    }

    @Transactional(readOnly = true)
    public Transaction getTransactionById(Long id) {
        return transactionRepository.findById(id).orElseThrow(()-> new AppException(ErrCode.TRANSACTION_NOT_FOUND));
    }
    @Transactional
    public Transaction saveTransaction(Transaction transaction) {
        if (transaction.getComponent() != null) {
            Long compId = transaction.getComponent().getId();
            if (compId == null || !componentRepository.existsById(compId)) {
                throw new AppException(ErrCode.COMPONENT_NOTFOUND);
            }
        }

        if (transaction.getProject() != null) {
            Long projId = transaction.getProject().getId();
            if (projId == null || !projectRepository.existsById(projId)) {
                throw new AppException(ErrCode.PROJECT_NOT_FOUND);
            }
        }

        return transactionRepository.save(transaction);
    }

    @Transactional
    public Transaction editTransactionById(Long id, Transaction updatedTransaction) {
        return transactionRepository.findById(id)
                .map(transaction -> {
                    transaction.setQuantity(updatedTransaction.getQuantity());
                    transaction.setTransactionType(updatedTransaction.getTransactionType());
                    transaction.setNote(updatedTransaction.getNote());
                    transaction.setComponent(updatedTransaction.getComponent());
                    // Lấy entity thật từ DB (nếu có)
                    if (updatedTransaction.getComponent() != null) {
                        Long compId = updatedTransaction.getComponent().getId();
                        Component component = componentRepository.findById(compId)
                                .orElseThrow(() -> new AppException(ErrCode.COMPONENT_NOTFOUND));
                        transaction.setComponent(component);
                    } else {
                        transaction.setComponent(null);
                    }

                    if (updatedTransaction.getProject() != null) {
                        Long projId = updatedTransaction.getProject().getId();
                        Project project = projectRepository.findById(projId)
                                .orElseThrow(() -> new AppException(ErrCode.PROJECT_NOT_FOUND));
                        transaction.setProject(project);
                    } else {
                        transaction.setProject(null);
                    }
                    return transactionRepository.save(transaction);
                })
                .orElseThrow(() -> new AppException(ErrCode.TRANSACTION_NOT_FOUND));
    }

    @Transactional
    public void deleteTransaction(Long id) {
        transactionRepository.deleteById(id);
    }
}
