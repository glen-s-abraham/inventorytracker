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
            // Get all production cycles for this room
            List<ProductionCycle> roomCycles = allCycles.stream()
                    .filter(c -> c.getGrowRoom().getId().equals(room.getId()))
                    .sorted(Comparator.comparing(ProductionCycle::getInoculationEndDate).reversed())
                    .collect(Collectors.toList());

            // Determine the last inoculation end date or use today
            LocalDate lastEnd = roomCycles.stream()
                    .map(ProductionCycle::getInoculationEndDate)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(LocalDate.now());

            LocalDate nextIdealDate = lastEnd.plusDays(1);
            List<SupplierOrderLineDTO> orderLines = new ArrayList<>();

            for (InventoryItem item : inventory) {
                double requiredQty = estimateRequirement(item);
                double availableQty = item.getQuantity();
                double shortfall = Math.max(0, requiredQty - availableQty);

                if (shortfall > 0) {
                    List<SupplierMapping> mappings = mappingRepo.findByItem(item);

                    for (SupplierMapping mapping : mappings) {
                        int leadTime = mapping.getSupplier().getAverageLeadTimeInDays(); // ✅ FROM SUPPLIER ENTITY
                        LocalDate suggestedDate = nextIdealDate.minusDays(leadTime);

                        orderLines.add(SupplierOrderLineDTO.builder()
                                .itemName(item.getName())
                                .unit(item.getUnit().toString())
                                .requiredQty(requiredQty)
                                .availableQty(availableQty)
                                .shortfall(shortfall)
                                .supplierName(mapping.getSupplier().getName())
                                .leadTimeDays(leadTime)
                                .suggestedOrderDate(suggestedDate.toString())
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

    private double estimateRequirement(InventoryItem item) {
        // Placeholder logic: Assume a static value per item for now
        return 5.0;
    }
}
