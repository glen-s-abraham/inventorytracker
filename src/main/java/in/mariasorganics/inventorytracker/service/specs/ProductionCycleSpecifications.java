package in.mariasorganics.inventorytracker.service.specs;

import in.mariasorganics.inventorytracker.entity.ProductionCycle;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class ProductionCycleSpecifications {

    private ProductionCycleSpecifications() { }

    /** Keyword search across code and remarks (case‑insensitive) */
    public static Specification<ProductionCycle> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            String like = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("code")), like),
                cb.like(cb.lower(root.get("remarks")), like)
            );
        };
    }

    /** Filter by inoculation start date range */
    public static Specification<ProductionCycle> inoculationStartBetween(LocalDate from, LocalDate to) {
        return dateBetween("inoculationStartDate", from, to);
    }

    /** Filter by inoculation end date range */
    public static Specification<ProductionCycle> inoculationEndBetween(LocalDate from, LocalDate to) {
        return dateBetween("inoculationEndDate", from, to);
    }

    /** Filter by fruiting start date range */
    public static Specification<ProductionCycle> fruitingStartBetween(LocalDate from, LocalDate to) {
        return dateBetween("fruitingStartDate", from, to);
    }

    /** Generic helper for date fields */
    private static Specification<ProductionCycle> dateBetween(String field,
                                                              LocalDate from,
                                                              LocalDate to) {
        return (root, query, cb) -> {
            if (from != null && to != null) {
                return cb.between(root.get(field), from, to);
            } else if (from != null) {
                return cb.greaterThanOrEqualTo(root.get(field), from);
            } else if (to != null) {
                return cb.lessThanOrEqualTo(root.get(field), to);
            }
            return null;
        };
    }

    /** Filter by status */
    public static Specification<ProductionCycle> statusIs(String status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    /** Filter by grow‑room ID */
    public static Specification<ProductionCycle> growRoomIdIs(Long growRoomId) {
        return (root, query, cb) ->
                growRoomId == null ? null : cb.equal(root.get("growRoom").get("id"), growRoomId);
    }
}
