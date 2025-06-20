package in.mariasorganics.inventorytracker.service.impl;

import in.mariasorganics.inventorytracker.dto.*;
import in.mariasorganics.inventorytracker.entity.*;
import in.mariasorganics.inventorytracker.repository.*;
import in.mariasorganics.inventorytracker.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements IDashboardService {

    private final GrowRoomRepository growRoomRepo;
    private final ProductionCycleRepository cycleRepo;
    private final InventoryItemRepository inventoryRepo;
    private final OrderReminderRepository reminderRepo;

    /* ---------- Summary cards ---------- */

    @Override
    public DashboardSummaryDTO getSummary() {

        long activeRooms = growRoomRepo.findAll().stream()
                .filter(GrowRoom::isActive)
                .count();

        long activeCycles = cycleRepo.countByStatus("ACTIVE");
        long lowInventory = inventoryRepo.countByQuantityLessThan(10);

        return DashboardSummaryDTO.builder()
                .totalRoomCount((int) activeRooms)
                .activeCycleCount((int) activeCycles)
                .lowInventoryCount((int) lowInventory)
                .build();
    }

    /* ---------- Calendar events ---------- */

    @Override
    public List<CalendarEventDTO> getCalendarEvents() {

        List<CalendarEventDTO> cycleEvents = cycleRepo.findAll().stream()
                .flatMap(cycle -> {
                    String room = cycle.getGrowRoom() != null
                            ? cycle.getGrowRoom().getName()
                            : "Unknown Room";

                    return Stream.of(
                            createEvent("Inoculation Start", room,
                                    cycle.getInoculationStartDate(), "inoculation-start"),
                            createEvent("Inoculation End", room,
                                    cycle.getInoculationEndDate(), "inoculation-end"),
                            createEvent("Fruiting Start", room,
                                    cycle.getFruitingStartDate(), "fruiting-start"),
                            createEvent("Fruiting End", room,
                                    cycle.getExpectedEndDate(), "fruiting-end"));
                })
                .filter(Objects::nonNull) // drop nulls (missing dates)
                .toList();

        List<CalendarEventDTO> reminderEvents = reminderRepo.findAll().stream()
                .map(r -> CalendarEventDTO.builder()
                        .title(r.getMaterialName() + " — Order (" + r.getSupplierName() + ")")
                        .date(r.getOrderDate())
                        .type("order")
                        .roomName(r.getGrowRoom().getName())
                        .color("#ff9800") // orange
                        .build())
                .toList();

        return Stream.concat(cycleEvents.stream(), reminderEvents.stream()).toList();

    }

    /* ---------- Helpers ---------- */

    private CalendarEventDTO createEvent(String label,
            String room,
            LocalDate date,
            String type) {

        if (date == null)
            return null;

        return CalendarEventDTO.builder()
                .title(room + " — " + label)
                .date(date)
                .type(type)
                .roomName(room)
                .color(getColorForEventType(type))
                .build();
    }

    private String getColorForEventType(String type) {
        return switch (type) {
            case "inoculation-start" -> "#28a745"; // green
            case "inoculation-end" -> "#66bb6a"; // light‑green
            case "fruiting-start" -> "#007bff"; // blue
            case "fruiting-end" -> "#6c757d"; // grey
            default -> "#ffc107"; // amber fallback
        };
    }
}
