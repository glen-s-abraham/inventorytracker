package in.mariasorganics.inventorytracker.service.specs;

import in.mariasorganics.inventorytracker.entity.InventoryItem;
import in.mariasorganics.inventorytracker.enums.UnitOfMeasurement;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class InventoryItemSpecifications {

    public static Specification<InventoryItem> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            String like = "%" + keyword.trim().toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), like),
                cb.like(cb.lower(root.get("remarks")), like)
            );
        };
    }

    public static Specification<InventoryItem> hasUnit(UnitOfMeasurement unit) {
        return (root, query, cb) ->
                unit == null ? null : cb.equal(root.get("unit"), unit);
    }

    public static Specification<InventoryItem> isLowStock(Boolean onlyLowStock) {
        return (root, query, cb) ->
                Boolean.TRUE.equals(onlyLowStock)
                        ? cb.lessThanOrEqualTo(root.get("quantity"), root.get("minimumStockLevel"))
                        : null;
    }

    public static Specification<InventoryItem> isExpired(Boolean onlyExpired) {
        return (root, query, cb) ->
                Boolean.TRUE.equals(onlyExpired)
                        ? cb.lessThan(root.get("expiryDate"), LocalDate.now())
                        : null;
    }

    public static Specification<InventoryItem> isExpiringSoon(Boolean onlyExpiringSoon, int daysThreshold) {
        return (root, query, cb) -> {
            if (Boolean.TRUE.equals(onlyExpiringSoon)) {
                LocalDate now = LocalDate.now();
                LocalDate soon = now.plusDays(daysThreshold);
                return cb.between(root.get("expiryDate"), now, soon);
            }
            return null;
        };
    }

    public static Specification<InventoryItem> hasPreferredSupplier(Long supplierId) {
    return (root, query, cb) -> {
        if (supplierId == null) return null;

        Join<Object, Object> mappingJoin = root.join("supplierMappings", JoinType.LEFT);
        Predicate supplierMatch = cb.equal(mappingJoin.get("supplier").get("id"), supplierId);
        Predicate isPreferred = cb.isTrue(mappingJoin.get("preferred"));

        return cb.and(supplierMatch, isPreferred);
    };
}

}
