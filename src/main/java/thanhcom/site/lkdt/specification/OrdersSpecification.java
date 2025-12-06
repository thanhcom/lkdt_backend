package thanhcom.site.lkdt.specification;

import org.springframework.data.jpa.domain.Specification;
import thanhcom.site.lkdt.entity.Orders;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class OrdersSpecification {

    public static Specification<Orders> hasCustomer(Long customerId) {
        return (root, query, cb) ->
                customerId == null ? null
                        : cb.equal(root.get("customer").get("id"), customerId);
    }

    public static Specification<Orders> hasMinTotal(BigDecimal min) {
        return (root, query, cb) ->
                min == null ? null
                        : cb.greaterThanOrEqualTo(root.get("totalAmount"), min);
    }

    public static Specification<Orders> hasMaxTotal(BigDecimal max) {
        return (root, query, cb) ->
                max == null ? null
                        : cb.lessThanOrEqualTo(root.get("totalAmount"), max);
    }

    public static Specification<Orders> dateFrom(OffsetDateTime from) {
        return (root, query, cb) ->
                from == null ? null
                        : cb.greaterThanOrEqualTo(root.get("orderDate"), from);
    }

    public static Specification<Orders> dateTo(OffsetDateTime to) {
        return (root, query, cb) ->
                to == null ? null
                        : cb.lessThanOrEqualTo(root.get("orderDate"), to);
    }

    public static Specification<Orders> hasOrderId(Long id) {
        return (root, query, cb) ->
                id == null ? null
                        : cb.equal(root.get("id"), id);
    }

    // Keyword tìm cả tên khách hàng và số điện thoại
    public static Specification<Orders> keyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;

            String likePattern = "%" + keyword.toLowerCase() + "%";

            return cb.or(
                    cb.like(cb.lower(root.get("customer").get("fullName")), likePattern),
                    cb.like(cb.lower(root.get("customer").get("phone")), likePattern)
            );
        };
    }

    // Tìm theo trạng thái
    public static Specification<Orders> hasStatus(String status) {
        return (root, query, cb) -> {
            if (status == null || status.isBlank()) return null;
            return cb.equal(root.get("status"), status);
        };
    }
}
