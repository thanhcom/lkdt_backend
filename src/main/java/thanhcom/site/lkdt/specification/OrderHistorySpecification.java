package thanhcom.site.lkdt.specification;

import org.springframework.data.jpa.domain.Specification;
import thanhcom.site.lkdt.entity.*;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import java.time.OffsetDateTime;

public class OrderHistorySpecification {

    public static Specification<OrderHistory> filter(
            Long customerId,
            String customerName,
            Long componentId,
            String componentName,
            OffsetDateTime createdFrom,
            OffsetDateTime createdTo,
            String action,
            String updatedBy
    ) {
        return (root, query, cb) -> {
            // DISTINCT để tránh duplicate do OneToMany join
            assert query != null;
            query.distinct(true);

            // JOIN để filter
            Join<OrderHistory, Orders> orderJoin = root.join("order", JoinType.LEFT);
            Join<Orders, Customer> customerJoin = orderJoin.join("customer", JoinType.LEFT);
            Join<Orders, OrderItem> orderItemJoin = orderJoin.join("items", JoinType.LEFT);
            Join<OrderItem, Component> componentJoin = orderItemJoin.join("component", JoinType.LEFT);

            var predicate = cb.conjunction();

            // CUSTOMER FILTER
            if (customerId != null) {
                predicate = cb.and(predicate, cb.equal(customerJoin.get("id"), customerId));
            }
            if (customerName != null && !customerName.isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(customerJoin.get("fullName")), "%" + customerName.toLowerCase() + "%")
                );
            }

            // COMPONENT FILTER
            if (componentId != null) {
                predicate = cb.and(predicate, cb.equal(componentJoin.get("id"), componentId));
            }
            if (componentName != null && !componentName.isEmpty()) {
                predicate = cb.and(predicate,
                        cb.like(cb.lower(componentJoin.get("name")), "%" + componentName.toLowerCase() + "%")
                );
            }

            // ORDER HISTORY FIELDS
            if (createdFrom != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("createdAt"), createdFrom));
            }
            if (createdTo != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("createdAt"), createdTo));
            }
            if (action != null && !action.isEmpty()) {
                predicate = cb.and(predicate, cb.equal(cb.lower(root.get("action")), action.toLowerCase()));
            }
            if (updatedBy != null && !updatedBy.isEmpty()) {
                predicate = cb.and(predicate, cb.like(cb.lower(root.get("updatedBy")), "%" + updatedBy.toLowerCase() + "%"));
            }

            return predicate;
        };
    }
}
