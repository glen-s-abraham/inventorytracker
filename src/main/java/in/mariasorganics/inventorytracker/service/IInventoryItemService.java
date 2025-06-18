package in.mariasorganics.inventorytracker.service;

import in.mariasorganics.inventorytracker.entity.InventoryItem;
import in.mariasorganics.inventorytracker.enums.UnitOfMeasurement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IInventoryItemService {
    Page<InventoryItem> findAll(
            String keyword,
            UnitOfMeasurement unit,
            Boolean lowStockOnly,
            Boolean expiredOnly,
            Boolean expiringSoon,
            int expiringThresholdDays,
            Long preferredSupplierId, // New param
            Pageable pageable);

    InventoryItem getById(Long id);

    InventoryItem save(InventoryItem item);

    void delete(Long id);
}
