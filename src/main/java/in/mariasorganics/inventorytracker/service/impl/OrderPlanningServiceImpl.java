package in.mariasorganics.inventorytracker.service.impl;

import in.mariasorganics.inventorytracker.dto.RoomMaterialPlanDTO;
import in.mariasorganics.inventorytracker.dto.SupplierOrderLineDTO;
import in.mariasorganics.inventorytracker.entity.*;
import in.mariasorganics.inventorytracker.repository.*;
import in.mariasorganics.inventorytracker.service.IOrderPlanningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

/**
 * Builds a separate order plan for each PLANNED production cycle,
 * showing required inventory items, shortfalls, and supplier-wise purchase dates.
 */
@Service
@RequiredArgsConstructor
public class OrderPlanningServiceImpl implements IOrderPlanningService {

    private final GrowRoomRepository growRoomRepo;
    private final InventoryItemRepository inventoryRepo;
    private final ProductionCycleRepository cycleRepo;
    private final SupplierMappingRepository mappingRepo;
    private final OrderReminderRepository reminderRepo;

    @Override
    public List<RoomMaterialPlanDTO> generateRoomWiseOrderPlan() {

        List<InventoryItem> allItems = inventoryRepo.findAll();
        List<ProductionCycle> allCycles = cycleRepo.findAll();

        List<RoomMaterialPlanDTO> plans = new ArrayList<>();

        for (ProductionCycle cycle : allCycles) {
            if (!"PLANNED".equalsIgnoreCase(cycle.getStatus())) continue;
            if (cycle.getInoculationStartDate() == null) continue;

            GrowRoom room = cycle.getGrowRoom();
            LocalDate inocStart = cycle.getInoculationStartDate();

            List<SupplierOrderLineDTO> orderLines = new ArrayList<>();

            for (InventoryItem item : allItems) {
                double required = estimateRequirement(item, List.of(cycle));
                double available = item.getQuantity();
                double shortfall = Math.max(0, required - available);

                List<SupplierMapping> mappings = mappingRepo.findByItem(item);

                if (mappings.isEmpty()) {
                    orderLines.add(blankLine(room, item, required, available, shortfall, inocStart));
                    continue;
                }

                for (SupplierMapping map : mappings) {
                    int leadDays = map.getSupplier().getAverageLeadTimeInDays();
                    LocalDate orderDate = inocStart.minusDays(leadDays);

                    boolean reminderSet = reminderRepo.existsByGrowRoom_IdAndMaterialNameAndSupplierNameAndOrderDate(
                            room.getId(),
                            item.getName(),
                            map.getSupplier().getName(),
                            orderDate
                    );

                    orderLines.add(SupplierOrderLineDTO.builder()
                            .itemName(item.getName())
                            .unit(item.getUnit().toString())
                            .requiredQty(required)
                            .availableQty(available)
                            .shortfall(shortfall)
                            .supplierName(map.getSupplier().getName())
                            .leadTimeDays(leadDays)
                            .suggestedOrderDate(orderDate)
                            .reminderExists(reminderSet)
                            .build());
                }
            }

            plans.add(RoomMaterialPlanDTO.builder()
                    .roomId(room.getId())
                    .roomName(room.getName() + " - " + cycle.getCode())
                    .nextInoculationIdealDate(inocStart)
                    .orderLines(orderLines)
                    .build());
        }
        plans.sort(Comparator.comparing(RoomMaterialPlanDTO::getNextInoculationIdealDate));
        return plans;
        
    }

    /** Estimate based on RawMaterialEstimate; fallback to 5 if unknown */
    private double estimateRequirement(InventoryItem item, List<ProductionCycle> cycles) {
        double total = 0.0;
        String key = item.getName().trim().toLowerCase();

        for (ProductionCycle cycle : cycles) {
            RawMaterialEstimate est = cycle.getEstimate();
            if (est != null) {
                switch (key) {
                    case "spawn" -> total += est.getTotalSpawn();
                    case "pellettes" -> total += est.getTotalSubstrate();
                    case "packaging" -> total += est.getTotalPackaging();
                    default -> total += 5.0;
                }
            } else {
                total += 5.0;
            }
        }
        return total;
    }

    /** Placeholder entry when no supplier is mapped */
    private SupplierOrderLineDTO blankLine(GrowRoom room,
                                           InventoryItem item,
                                           double required,
                                           double available,
                                           double shortfall,
                                           LocalDate inocStart) {

        boolean reminderSet = reminderRepo.existsByGrowRoom_IdAndMaterialNameAndSupplierNameAndOrderDate(
                room.getId(), item.getName(), "—", inocStart
        );

        return SupplierOrderLineDTO.builder()
                .itemName(item.getName())
                .unit(item.getUnit().toString())
                .requiredQty(required)
                .availableQty(available)
                .shortfall(shortfall)
                .supplierName("—")
                .leadTimeDays(0)
                .suggestedOrderDate(inocStart)
                .reminderExists(reminderSet)
                .build();
    }
}
