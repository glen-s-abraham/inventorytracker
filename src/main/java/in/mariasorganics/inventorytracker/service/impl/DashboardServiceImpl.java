package in.mariasorganics.inventorytracker.service.impl;

import in.mariasorganics.inventorytracker.dto.CalendarEventDTO;
import in.mariasorganics.inventorytracker.dto.DashboardSummaryDTO;
import in.mariasorganics.inventorytracker.entity.GrowRoom;
import in.mariasorganics.inventorytracker.entity.ProductionCycle;
import in.mariasorganics.inventorytracker.repository.GrowRoomRepository;
import in.mariasorganics.inventorytracker.repository.ProductionCycleRepository;
import in.mariasorganics.inventorytracker.repository.InventoryItemRepository;
import in.mariasorganics.inventorytracker.repository.SupplierMappingRepository;
import in.mariasorganics.inventorytracker.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final GrowRoomRepository growRoomRepo;
    private final ProductionCycleRepository cycleRepo;
    private final InventoryItemRepository inventoryRepo;
    private final SupplierMappingRepository supplierMappingRepo;

    @Override
    public DashboardSummaryDTO getSummary() {

        long roomCnt = growRoomRepo.findAll().stream()
                .filter(GrowRoom::isActive)
                .count(); // or countByActiveTrue()

        long cycleCnt = cycleRepo.countByStatus("ACTIVE");
        long lowStock = inventoryRepo.countByQuantityLessThan(10); // threshold

        return DashboardSummaryDTO.builder()
                .totalRoomCount((int) roomCnt)
                .activeCycleCount((int) cycleCnt)
                .lowInventoryCount((int) lowStock)
                .build();
    }

    @Override
    public List<CalendarEventDTO> getCalendarEvents() {
        List<ProductionCycle> cycles = cycleRepo.findAll();

        return cycles.stream()
                .filter(c -> c.getInoculationStartDate() != null)
                .map(c -> CalendarEventDTO.builder()
                        .title(c.getGrowRoom().getName() + " — Inoculation")
                        .date(c.getInoculationStartDate())
                        .type("inoculation")
                        .build())
                .collect(Collectors.toList());
    }
}
