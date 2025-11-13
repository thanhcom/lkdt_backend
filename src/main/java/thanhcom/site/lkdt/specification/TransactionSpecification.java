package thanhcom.site.lkdt.specification;

import org.springframework.data.jpa.domain.Specification;
import thanhcom.site.lkdt.entity.Component;
import thanhcom.site.lkdt.entity.Project;
import thanhcom.site.lkdt.entity.Transaction;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.time.OffsetDateTime;

public class TransactionSpecification {

    /**
     * Lọc theo tên linh kiện (Component)
     */
    public static Specification<Transaction> hasComponentName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isEmpty()) return null;
            Join<Transaction, Component> component = root.join("component", JoinType.LEFT);
            return cb.like(cb.lower(component.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    /**
     * Lọc theo ID linh kiện (Component)
     */
    public static Specification<Transaction> hasComponentId(Long componentId) {
        return (root, query, cb) -> {
            if (componentId == null) return null;
            Join<Transaction, Component> component = root.join("component", JoinType.LEFT);
            return cb.equal(component.get("id"), componentId);
        };
    }

    /**
     * Lọc theo tên dự án (Project)
     */
    public static Specification<Transaction> hasProjectName(String name) {
        return (root, query, cb) -> {
            if (name == null || name.isEmpty()) return null;
            Join<Transaction, Project> project = root.join("project", JoinType.LEFT);
            return cb.like(cb.lower(project.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    /**
     * Lọc theo ID dự án (Project)
     */
    public static Specification<Transaction> hasProjectId(Long projectId) {
        return (root, query, cb) -> {
            if (projectId == null) return null;
            Join<Transaction, Project> project = root.join("project", JoinType.LEFT);
            return cb.equal(project.get("id"), projectId);
        };
    }

    /**
     * Lọc theo loại giao dịch (TransactionType)
     */
    public static Specification<Transaction> hasTransactionType(String type) {
        return (root, query, cb) -> {
            if (type == null || type.isEmpty()) return null;
            return cb.equal(root.get("transactionType").as(String.class), type);
        };
    }

    /**
     * Lọc theo ngày giao dịch trong khoảng [start, end]
     */
    public static Specification<Transaction> betweenDates(OffsetDateTime start, OffsetDateTime end) {
        return (root, query, cb) -> {
            if (start == null && end == null) return null;
            if (start != null && end != null)
                return cb.between(root.get("transactionDate"), start, end);
            if (start != null)
                return cb.greaterThanOrEqualTo(root.get("transactionDate"), start);
            else
                return cb.lessThanOrEqualTo(root.get("transactionDate"), end);
        };
    }
}
