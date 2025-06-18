package in.mariasorganics.inventorytracker.service.specs;

import in.mariasorganics.inventorytracker.entity.RawMaterialEstimate;
import org.springframework.data.jpa.domain.Specification;

public class RawMaterialEstimateSpecifications {

    public static Specification<RawMaterialEstimate> hasCycleId(Long cycleId) {
        return (root, query, builder) ->
                cycleId == null ? null : builder.equal(root.get("cycle").get("id"), cycleId);
    }

    public static Specification<RawMaterialEstimate> hasRemarksContaining(String keyword) {
        return (root, query, builder) ->
                keyword == null || keyword.trim().isEmpty()
                        ? null
                        : builder.like(builder.lower(root.get("remarks")), "%" + keyword.toLowerCase() + "%");
    }
}
