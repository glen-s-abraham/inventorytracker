package in.mariasorganics.inventorytracker.service;

import in.mariasorganics.inventorytracker.entity.RawMaterialEstimate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface IRawMaterialEstimateService {
    
    RawMaterialEstimate save(RawMaterialEstimate estimate);

    Optional<RawMaterialEstimate> findById(Long id);

    Page<RawMaterialEstimate> findAll(String keyword, Long cycleId, Pageable pageable);

    void deleteById(Long id);
}
