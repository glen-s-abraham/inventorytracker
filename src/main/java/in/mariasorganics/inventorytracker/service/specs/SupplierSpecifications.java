package in.mariasorganics.inventorytracker.service.specs;

import in.mariasorganics.inventorytracker.entity.Supplier;
import org.springframework.data.jpa.domain.Specification;

public class SupplierSpecifications {

    public static Specification<Supplier> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.trim().isEmpty()) return null;
            String like = "%" + keyword.trim().toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), like),
                cb.like(cb.lower(root.get("contactInfo")), like),
                cb.like(cb.lower(root.get("remarks")), like)
            );
        };
    }

    public static Specification<Supplier> hasMinRating(Double minRating) {
        return (root, query, cb) ->
                (minRating == null) ? null : cb.greaterThanOrEqualTo(root.get("rating"), minRating);
    }

    public static Specification<Supplier> hasMaxPrice(Double maxPrice) {
        return (root, query, cb) ->
                (maxPrice == null) ? null : cb.lessThanOrEqualTo(root.get("pricePerUnit"), maxPrice);
    }

    public static Specification<Supplier> hasMaxLeadTime(Integer maxLeadTime) {
        return (root, query, cb) ->
                (maxLeadTime == null) ? null : cb.lessThanOrEqualTo(root.get("averageLeadTimeInDays"), maxLeadTime);
    }
}
