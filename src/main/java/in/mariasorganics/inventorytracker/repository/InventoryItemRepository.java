package in.mariasorganics.inventorytracker.repository;

import in.mariasorganics.inventorytracker.entity.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long>, JpaSpecificationExecutor<InventoryItem> {
    long countByQuantityLessThan(double threshold);
}
