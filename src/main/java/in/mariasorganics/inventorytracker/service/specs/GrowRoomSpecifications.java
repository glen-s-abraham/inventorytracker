package in.mariasorganics.inventorytracker.service.specs;

import in.mariasorganics.inventorytracker.entity.GrowRoom;
import org.springframework.data.jpa.domain.Specification;

public final class GrowRoomSpecifications {

    private GrowRoomSpecifications() { }

    public static Specification<GrowRoom> hasKeyword(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            String like = "%" + keyword.toLowerCase() + "%";
            return cb.or(
                cb.like(cb.lower(root.get("name")), like),
                cb.like(cb.lower(root.get("notes")), like)
            );
        };
    }

    public static Specification<GrowRoom> isActive(Boolean active) {
        return (root, query, cb) ->
                active == null ? null : cb.equal(root.get("active"), active);
    }
}