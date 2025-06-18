// src/main/java/in/mariasorganics/inventorytracker/repository/ProductionCycleRepository.java
package in.mariasorganics.inventorytracker.repository;

import in.mariasorganics.inventorytracker.entity.ProductionCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductionCycleRepository
        extends JpaRepository<ProductionCycle, Long>, JpaSpecificationExecutor<ProductionCycle> {
}
