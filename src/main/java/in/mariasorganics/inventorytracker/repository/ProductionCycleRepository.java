// src/main/java/in/mariasorganics/inventorytracker/repository/ProductionCycleRepository.java
package in.mariasorganics.inventorytracker.repository;

import in.mariasorganics.inventorytracker.entity.ProductionCycle;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProductionCycleRepository
                extends JpaRepository<ProductionCycle, Long>, JpaSpecificationExecutor<ProductionCycle> {
        long countByStatus(String status); // ➕ active cycles for dashboard

        List<ProductionCycle> findByInoculationStartDateBetween(LocalDate start, LocalDate end);

        List<ProductionCycle> findByInoculationEndDateBetween(LocalDate start, LocalDate end);
}
