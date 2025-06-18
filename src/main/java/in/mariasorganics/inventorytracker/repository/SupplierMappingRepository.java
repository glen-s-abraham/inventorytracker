package in.mariasorganics.inventorytracker.repository;

import in.mariasorganics.inventorytracker.entity.SupplierMapping;
import in.mariasorganics.inventorytracker.entity.SupplierMappingId;
import in.mariasorganics.inventorytracker.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SupplierMappingRepository extends JpaRepository<SupplierMapping, SupplierMappingId> {
    void deleteByItem(InventoryItem item);
}
