package in.mariasorganics.inventorytracker.service.impl;

import in.mariasorganics.inventorytracker.dto.RoomMaterialPlanDTO;
import in.mariasorganics.inventorytracker.dto.SupplierOrderLineDTO;
import in.mariasorganics.inventorytracker.entity.GrowRoom;
import in.mariasorganics.inventorytracker.entity.InventoryItem;
import in.mariasorganics.inventorytracker.entity.ProductionCycle;
import in.mariasorganics.inventorytracker.entity.SupplierMapping;
import in.mariasorganics.inventorytracker.repository.GrowRoomRepository;
import in.mariasorganics.inventorytracker.repository.InventoryItemRepository;
import in.mariasorganics.inventorytracker.repository.ProductionCycleRepository;
import in.mariasorganics.inventorytracker.repository.SupplierMappingRepository;
import in.mariasorganics.inventorytracker.service.IOrderPlanningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Generates a per‑room material plan that **always lists every inventory
 * item**.
 * <ul>
 * <li>If no supplier mapping exists, a single line is shown with “—”.</li>
 * <li>If no short‑fall exists, the line is still rendered (shortfall = 0).</li>
 * <li>Lead‑time comes from <code>Supplier.averageLeadTimeInDays</code>.</li>
 * <li>The order date is <code>nextIdealInoculationDate – leadTime</code>.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class OrderPlanningServiceImpl implements IOrderPlanningService {

    private final GrowRoomRepository growRoomRepo;
    private final InventoryItemRepository inventoryRepo;
    private final ProductionCycleRepository cycleRepo;
    private final SupplierMappingRepository mappingRepo;

    @Override
    public List<RoomMaterialPlanDTO> generateRoomWiseOrderPlan() {
        List<GrowRoom> rooms = growRoomRepo.findAll();
        List<InventoryItem> inventory = inventoryRepo.findAll();
        List<ProductionCycle> allCycles = cycleRepo.findAll();

        List<RoomMaterialPlanDTO> plans = new ArrayList<>();

        for (GrowRoom room : rooms) {
            // 🔎 only ACTIVE cycles for this room
            List<ProductionCycle> roomCycles = allCycles.stream()
                    .filter(c -> c.getGrowRoom().getId().equals(room.getId()))
                    .filter(c -> "ACTIVE".equalsIgnoreCase(c.getStatus()))
                    .sorted(Comparator.comparing(ProductionCycle::getInoculationEndDate).reversed())
                    .collect(Collectors.toList());

            if (roomCycles.isEmpty()) {
                // skip rooms that aren’t in production
                continue;
            }

            // last (most‑recent) inoculation‑end ‑> ideal next start = +1 day
            LocalDate lastEnd = roomCycles.stream()
                    .map(ProductionCycle::getInoculationEndDate)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(LocalDate.now());

            LocalDate nextIdealDate = lastEnd.plusDays(1);
            List<SupplierOrderLineDTO> orderLines = new ArrayList<>();

            for (InventoryItem item : inventory) {
                double required = estimateRequirement(item, roomCycles); // could be 0
                double available = item.getQuantity();
                double shortfall = Math.max(0, required - available);

                List<SupplierMapping> mappings = mappingRepo.findByItem(item);

                if (mappings.isEmpty()) {
                    // show a placeholder row so the material is visible
                    orderLines.add(blankLine(item, required, available, shortfall, nextIdealDate));
                } else {
                    for (SupplierMapping m : mappings) {
                        int lead = m.getSupplier().getAverageLeadTimeInDays();
                        orderLines.add(SupplierOrderLineDTO.builder()
                                .itemName(item.getName())
                                .unit(item.getUnit().toString())
                                .requiredQty(required)
                                .availableQty(available)
                                .shortfall(shortfall)
                                .supplierName(m.getSupplier().getName())
                                .leadTimeDays(lead)
                                .suggestedOrderDate(nextIdealDate.minusDays(lead).toString())
                                .build());
                    }
                }
            }

            plans.add(RoomMaterialPlanDTO.builder()
                    .roomId(room.getId())
                    .roomName(room.getName())
                    .nextInoculationIdealDate(nextIdealDate)
                    .orderLines(orderLines)
                    .build());
        }
        return plans;
    }

    /**
     * Sum the requirement of this item across every active cycle for the room.
     * (replace this stub with real logic / RawMaterialEstimate lookup if needed)
     */
    private double estimateRequirement(InventoryItem item, List<ProductionCycle> cycles) {
        // TODO pull raw‑material estimates; fallback → 5 kg / pcs per cycle
        return cycles.size() * 5.0;
    }

    private SupplierOrderLineDTO blankLine(InventoryItem item, double required, double available,
            double shortfall, LocalDate nextIdealDate) {
        return SupplierOrderLineDTO.builder()
                .itemName(item.getName())
                .unit(item.getUnit().toString())
                .requiredQty(required)
                .availableQty(available)
                .shortfall(shortfall)
                .supplierName("—")
                .leadTimeDays(0)
                .suggestedOrderDate(nextIdealDate.toString())
                .build();
    }
}
