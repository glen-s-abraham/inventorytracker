package in.mariasorganics.inventorytracker.repository;

import in.mariasorganics.inventorytracker.entity.SupplierMapping;
import in.mariasorganics.inventorytracker.entity.SupplierMappingId;
import in.mariasorganics.inventorytracker.entity.InventoryItem;
import in.mariasorganics.inventorytracker.entity.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupplierMappingRepository extends JpaRepository<SupplierMapping, SupplierMappingId> {

    // For cleanup
    void deleteByItem(InventoryItem item);

    // Optional: For future filtering
    List<SupplierMapping> findByItem(InventoryItem item);

    List<SupplierMapping> findBySupplier(Supplier supplier);
}
