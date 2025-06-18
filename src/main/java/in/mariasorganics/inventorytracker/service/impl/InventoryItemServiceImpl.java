package in.mariasorganics.inventorytracker.service.impl;

import in.mariasorganics.inventorytracker.entity.InventoryItem;
import in.mariasorganics.inventorytracker.enums.UnitOfMeasurement;
import in.mariasorganics.inventorytracker.repository.InventoryItemRepository;
import in.mariasorganics.inventorytracker.service.IInventoryItemService;
import in.mariasorganics.inventorytracker.service.specs.InventoryItemSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryItemServiceImpl implements IInventoryItemService {

    private final InventoryItemRepository repository;

    /**
     * Returns a paginated, filtered list of inventory items.
     *
     * @param keyword               full‑text search (name / remarks)
     * @param unit                  filter by unit of measurement
     * @param lowStockOnly          flag → show only items below minimum‑stock level
     * @param expiredOnly           flag → show only expired items
     * @param expiringSoon          flag → show items that will expire within N days
     * @param expiringThresholdDays N days used with {@code expiringSoon}
     * @param preferredSupplierId   filter by preferred supplier (in SupplierMapping)
     * @param pageable              page + size + sort
     */
    @Override
    public Page<InventoryItem> findAll(
            String keyword,
            UnitOfMeasurement unit,
            Boolean lowStockOnly,
            Boolean expiredOnly,
            Boolean expiringSoon,
            int expiringThresholdDays,
            Long preferredSupplierId,
            Pageable pageable) {

        Specification<InventoryItem> spec = Specification
                .where(InventoryItemSpecifications.hasKeyword(keyword))
                .and(InventoryItemSpecifications.hasUnit(unit))
                .and(InventoryItemSpecifications.isLowStock(lowStockOnly))
                .and(InventoryItemSpecifications.isExpired(expiredOnly))
                .and(InventoryItemSpecifications.isExpiringSoon(expiringSoon, expiringThresholdDays))
                .and(InventoryItemSpecifications.hasPreferredSupplier(preferredSupplierId)); // NEW

        return repository.findAll(spec, pageable);
    }

    /* --------------------------------------------------------------------- */

    @Override
    public InventoryItem getById(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @Override
    public InventoryItem save(InventoryItem item) {
        return repository.save(item);
    }

    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}
