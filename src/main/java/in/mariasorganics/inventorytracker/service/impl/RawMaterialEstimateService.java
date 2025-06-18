package in.mariasorganics.inventorytracker.service.impl;

import in.mariasorganics.inventorytracker.entity.RawMaterialEstimate;
import in.mariasorganics.inventorytracker.repository.RawMaterialEstimateRepository;
import in.mariasorganics.inventorytracker.service.IRawMaterialEstimateService;
import in.mariasorganics.inventorytracker.service.specs.RawMaterialEstimateSpecifications;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RawMaterialEstimateService implements IRawMaterialEstimateService {

    @Autowired
    private RawMaterialEstimateRepository repository;

    @Override
    public RawMaterialEstimate save(RawMaterialEstimate estimate) {
        // Auto-calculate totals
        double totalSpawn = estimate.getNumberOfBags() * estimate.getSpawnPerBag();
        double totalSubstrate = estimate.getNumberOfBags() * estimate.getSubstratePerBag();
        int totalPackaging = estimate.getNumberOfBags() * estimate.getPackagingPerBag();

        estimate.setTotalSpawn(totalSpawn);
        estimate.setTotalSubstrate(totalSubstrate);
        estimate.setTotalPackaging(totalPackaging);

        return repository.save(estimate);
    }

    @Override
    public Optional<RawMaterialEstimate> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Page<RawMaterialEstimate> findAll(String keyword, Long cycleId, Pageable pageable) {
        Specification<RawMaterialEstimate> spec = Specification.where(
            RawMaterialEstimateSpecifications.hasCycleId(cycleId)
        ).and(
            RawMaterialEstimateSpecifications.hasRemarksContaining(keyword)
        );

        return repository.findAll(spec, pageable);
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}
