package thanhcom.site.lkdt.specification;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import thanhcom.site.lkdt.entity.Component;
import thanhcom.site.lkdt.entity.Supplier;

import java.util.ArrayList;
import java.util.List;

public class SupplierSpecification {

    /**
     * Quick search: tìm kiếm keyword trong nhiều field text
     */
    public static Specification<Supplier> quickSearch(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (!(keyword != null && !keyword.isEmpty())) {
                return criteriaBuilder.conjunction(); // trả về tất cả nếu không có keyword
            }

            String likePattern = "%" + keyword.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("contact")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("phone")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("address")), likePattern));
            // OR giữa các field
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filter nâng cao: kết hợp quick search + các filter khác
     */
    public static Specification<Supplier> filter(
            String keyword,
            Long id
    ) {
        return (root, query, criteriaBuilder) -> {
            ArrayList<Predicate> predicates = new ArrayList<>();

            // Quick search
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(quickSearch(keyword).toPredicate(root, query, criteriaBuilder));
            }

            // Filter ID
            if (id != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
