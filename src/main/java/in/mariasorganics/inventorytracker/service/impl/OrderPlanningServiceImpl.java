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
 * Builds a room‑wise purchase plan that **always shows every inventory item** –
 * even when no supplier is mapped or no short‑fall exists.  
 * A button in the view lets the user add / remove a reminder for each line;  
 * that state is surfaced via {@code SupplierOrderLineDTO.reminderExists}.
 */
@Service
@RequiredArgsConstructor
public class OrderPlanningServiceImpl implements IOrderPlanningService {

    private final GrowRoomRepository        growRoomRepo;
    private final InventoryItemRepository   inventoryRepo;
    private final ProductionCycleRepository cycleRepo;
    private final SupplierMappingRepository mappingRepo;
    private final OrderReminderRepository   reminderRepo;   // ← NEW

    // --------------------------------------------------------------------- //
    //  Public API
    // --------------------------------------------------------------------- //

    @Override
    public List<RoomMaterialPlanDTO> generateRoomWiseOrderPlan() {

        List<GrowRoom>        rooms      = growRoomRepo.findAll();
        List<InventoryItem>   allItems   = inventoryRepo.findAll();
        List<ProductionCycle> allCycles  = cycleRepo.findAll();

        List<RoomMaterialPlanDTO> result = new ArrayList<>();

        for (GrowRoom room : rooms) {

            // --- pick ACTIVE cycles for this room -----------------------------------------
            List<ProductionCycle> roomCycles = allCycles.stream()
                    .filter(c -> Objects.equals(c.getGrowRoom().getId(), room.getId()))
                    .filter(c -> "ACTIVE".equalsIgnoreCase(c.getStatus()))
                    .sorted(Comparator.comparing(ProductionCycle::getInoculationEndDate).reversed())
                    .toList();

            if (roomCycles.isEmpty()) continue;               // idle room – skip

            // --- derive “next ideal inoculation start” ------------------------------------
            LocalDate lastInocEnd = roomCycles.stream()
                    .map(ProductionCycle::getInoculationEndDate)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(LocalDate.now());

            LocalDate nextIdealStart = lastInocEnd.plusDays(1);

            // --- build order lines --------------------------------------------------------
            List<SupplierOrderLineDTO> orderLines = new ArrayList<>();

            for (InventoryItem item : allItems) {

                double required   = estimateRequirement(item, roomCycles);
                double available  = item.getQuantity();
                double shortfall  = Math.max(0, required - available);

                List<SupplierMapping> mappings = mappingRepo.findByItem(item);

                if (mappings.isEmpty()) {
                    orderLines.add(
                            blankLine(room, item, required, available,
                                      shortfall, nextIdealStart));
                    continue;
                }

                for (SupplierMapping map : mappings) {

                    int      leadDays  = map.getSupplier().getAverageLeadTimeInDays();
                    LocalDate orderDate = nextIdealStart.minusDays(leadDays);

                    boolean reminderSet =
                            reminderRepo.existsByGrowRoom_IdAndMaterialNameAndSupplierNameAndOrderDate(
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

            // ------- add plan for this room ----------------------------------------------
            result.add(RoomMaterialPlanDTO.builder()
                    .roomId(room.getId())
                    .roomName(room.getName())
                    .nextInoculationIdealDate(nextIdealStart)
                    .orderLines(orderLines)
                    .build());
        }
        return result;
    }

    // --------------------------------------------------------------------- //
    //  Helpers
    // --------------------------------------------------------------------- //

    /**
     * Crude requirement estimate:  5 units per active cycle.
     * Replace with a real formula or DB lookup when available.
     */
    private double estimateRequirement(InventoryItem item,
                                       List<ProductionCycle> cycles) {
        return cycles.size() * 5.0;
    }

    /**
     * Creates the placeholder “no supplier” row so the material
     * is never hidden from the operator.
     */
    private SupplierOrderLineDTO blankLine(GrowRoom room,
                                           InventoryItem item,
                                           double required,
                                           double available,
                                           double shortfall,
                                           LocalDate orderDate) {

        boolean reminderSet =
                reminderRepo.existsByGrowRoom_IdAndMaterialNameAndSupplierNameAndOrderDate(
                        room.getId(), item.getName(), "—", orderDate);

        return SupplierOrderLineDTO.builder()
                .itemName(item.getName())
                .unit(item.getUnit().toString())
                .requiredQty(required)
                .availableQty(available)
                .shortfall(shortfall)
                .supplierName("—")
                .leadTimeDays(0)
                .suggestedOrderDate(orderDate)
                .reminderExists(reminderSet)
                .build();
    }
}
