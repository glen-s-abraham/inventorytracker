package in.mariasorganics.inventorytracker.service.impl;

import in.mariasorganics.inventorytracker.dto.OrderPlanEntry;
import in.mariasorganics.inventorytracker.entity.InventoryItem;
import in.mariasorganics.inventorytracker.entity.ProductionCycle;
import in.mariasorganics.inventorytracker.entity.Supplier;
import in.mariasorganics.inventorytracker.repository.InventoryItemRepository;
import in.mariasorganics.inventorytracker.repository.ProductionCycleRepository;
import in.mariasorganics.inventorytracker.repository.SupplierRepository;
import in.mariasorganics.inventorytracker.service.IOrderPlanningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
public class OrderPlanningServiceImpl implements IOrderPlanningService {

    private final InventoryItemRepository inventoryRepo;
    private final SupplierRepository supplierRepo;
    private final ProductionCycleRepository cycleRepo;

    @Override
    public List<OrderPlanEntry> generateOrderPlan() {
        List<InventoryItem> inventoryItems = inventoryRepo.findAll();
        List<ProductionCycle> upcomingCycles = cycleRepo.findAll(); // You may filter ACTIVE/PLANNED
        List<Supplier> allSuppliers = supplierRepo.findAll();

        List<OrderPlanEntry> planEntries = new ArrayList<>();

        for (InventoryItem item : inventoryItems) {
            double requiredQuantity = estimateRequiredQuantity(item, upcomingCycles);
            double available = item.getQuantity();
            double toOrder = Math.max(0, requiredQuantity - available);

            Optional<Supplier> preferredSupplier = allSuppliers.stream()
                .filter(s -> item.getName().equalsIgnoreCase(s.getName())) // Simplified logic
                .findFirst();

            int leadTime = preferredSupplier.map(Supplier::getAverageLeadTimeInDays).orElse(3);
            String supplierName = preferredSupplier.map(Supplier::getName).orElse("—");

            LocalDate targetDate = getNearestCycleDate(upcomingCycles);
            LocalDate suggestedOrderDate = targetDate.minusDays(leadTime + 2); // +2 for buffer

            planEntries.add(OrderPlanEntry.builder()
                    .itemName(item.getName())
                    .unit(item.getUnit().toString())
                    .totalRequired(requiredQuantity)
                    .availableStock(available)
                    .quantityToOrder(toOrder)
                    .supplierName(supplierName)
                    .leadTimeDays(leadTime)
                    .suggestedOrderDate(suggestedOrderDate.toString())
                    .build());
        }

        return planEntries;
    }

    private double estimateRequiredQuantity(InventoryItem item, List<ProductionCycle> cycles) {
        // Placeholder: assume 1 unit per cycle
        return (double) cycles.size();
    }

    private LocalDate getNearestCycleDate(List<ProductionCycle> cycles) {
        return cycles.stream()
                .map(ProductionCycle::getInoculationStartDate)
                .filter(Objects::nonNull)
                .min(LocalDate::compareTo)
                .orElse(LocalDate.now().plusDays(7));
    }
}
