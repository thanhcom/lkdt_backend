package thanhcom.site.lkdt.specification;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import thanhcom.site.lkdt.entity.Component;

import java.util.ArrayList;
import java.util.List;

public class ComponentSpecification {

    /**
     * Quick search: tìm kiếm keyword trong nhiều field text
     */
    public static Specification<Component> quickSearch(String keyword) {
        return (root, query, criteriaBuilder) -> {
            if (keyword == null || keyword.isEmpty()) {
                return criteriaBuilder.conjunction(); // trả về tất cả nếu không có keyword
            }

            String likePattern = "%" + keyword.toLowerCase() + "%";
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("type")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("manufacturer")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("packageField")), likePattern));
            predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), likePattern));

            // OR giữa các field
            return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Filter nâng cao: kết hợp quick search + các filter khác
     */
    public static Specification<Component> filter(
            String keyword,
            Long id,
            Integer stockQuantity
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Quick search
            if (keyword != null && !keyword.isEmpty()) {
                predicates.add(quickSearch(keyword).toPredicate(root, query, criteriaBuilder));
            }

            // Filter ID
            if (id != null) {
                predicates.add(criteriaBuilder.equal(root.get("id"), id));
            }

            // Filter stockQuantity
            if (stockQuantity != null) {
                predicates.add(criteriaBuilder.equal(root.get("stockQuantity"), stockQuantity));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
