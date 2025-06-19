package in.mariasorganics.inventorytracker.repository;

import in.mariasorganics.inventorytracker.entity.RawMaterialEstimate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RawMaterialEstimateRepository extends JpaRepository<RawMaterialEstimate, Long>, JpaSpecificationExecutor<RawMaterialEstimate> {
    RawMaterialEstimate findByCycleId(Long cycleId); // Optional helper
    
}
