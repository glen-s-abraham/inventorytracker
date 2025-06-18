package in.mariasorganics.inventorytracker.service;

import in.mariasorganics.inventorytracker.entity.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ISupplierService {

    Page<Supplier> findAll(String keyword, Double minRating, Double maxPrice, Integer maxLeadTime, Pageable pageable);

    Supplier getById(Long id);

    Supplier save(Supplier supplier);

    void delete(Long id);

    List<Supplier> findAll();
}
