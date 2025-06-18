package in.mariasorganics.inventorytracker.service.impl;

import in.mariasorganics.inventorytracker.entity.Supplier;
import in.mariasorganics.inventorytracker.repository.SupplierRepository;
import in.mariasorganics.inventorytracker.service.ISupplierService;
import in.mariasorganics.inventorytracker.service.specs.SupplierSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements ISupplierService {

    private final SupplierRepository repository;

    @Override
    public Page<Supplier> findAll(String keyword, Double minRating, Double maxPrice, Integer maxLeadTime, Pageable pageable) {
        Specification<Supplier> spec = Specification
                .where(SupplierSpecifications.hasKeyword(keyword))
                .and(SupplierSpecifications.hasMinRating(minRating))
                .and(SupplierSpecifications.hasMaxPrice(maxPrice))
                .and(SupplierSpecifications.hasMaxLeadTime(maxLeadTime));

        return repository.findAll(spec, pageable);
    }

    @Override
    public Supplier getById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @Override
    public Supplier save(Supplier supplier) {
        return repository.save(supplier);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Supplier> findAll() {
        return repository.findAll();
    }
}
