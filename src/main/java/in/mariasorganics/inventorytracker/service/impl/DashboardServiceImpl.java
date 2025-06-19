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
import java.util.stream.Stream;

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
                                .flatMap((ProductionCycle cycle) -> {
                                        String roomName = cycle.getGrowRoom() != null ? cycle.getGrowRoom().getName()
                                                        : "Unknown Room";

                                        return Stream.of(
                                                        createEvent("Inoculation Start", roomName,
                                                                        cycle.getInoculationStartDate(),
                                                                        "inoculation-start"),
                                                        createEvent("Inoculation End", roomName,
                                                                        cycle.getInoculationEndDate(),
                                                                        "inoculation-end"),
                                                        createEvent("Fruiting Start", roomName,
                                                                        cycle.getFruitingStartDate(), "fruiting-start"),
                                                        createEvent("Fruiting End", roomName,
                                                                        cycle.getExpectedEndDate(), "fruiting-end"));
                                })
                                .filter(e -> e.getDate() != null)
                                .collect(Collectors.toList());

        }

        private CalendarEventDTO createEvent(String label, String room, LocalDate date, String type) {
                if (date == null)
                        return null;

                return CalendarEventDTO.builder()
                                .title(room + " — " + label)
                                .date(date)
                                .type(type)
                                .roomName(room)
                                .color(getColorForEventType(type)) // 🔥 Add color dynamically
                                .build();
        }

        private String getColorForEventType(String type) {
                return switch (type) {
                        case "inoculation-start" -> "#28a745"; // green
                        case "inoculation-end" -> "#66bb6a"; // light green
                        case "fruiting-start" -> "#007bff"; // blue
                        case "fruiting-end" -> "#6c757d"; // grey
                        default -> "#ffc107"; // amber for unknown/fallback
                };
        }

}
