package in.mariasorganics.inventorytracker.controller;

import in.mariasorganics.inventorytracker.dto.CalendarEventDTO;
import in.mariasorganics.inventorytracker.dto.DashboardSummaryDTO;
import in.mariasorganics.inventorytracker.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final IDashboardService dashboardService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryDTO> getSummary() {
        DashboardSummaryDTO summary = dashboardService.getSummary();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/calendar")
    public ResponseEntity<List<CalendarEventDTO>> getCalendarEvents() {
        List<CalendarEventDTO> events = dashboardService.getCalendarEvents();
        return ResponseEntity.ok(events);
    }
}
