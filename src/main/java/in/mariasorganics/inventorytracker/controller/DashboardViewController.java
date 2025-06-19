package in.mariasorganics.inventorytracker.controller;

import in.mariasorganics.inventorytracker.dto.CalendarEventDTO;
import in.mariasorganics.inventorytracker.dto.DashboardSummaryDTO;
import in.mariasorganics.inventorytracker.service.IDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class DashboardViewController {

    private final IDashboardService dashboardService;

    @GetMapping("/dashboard")
    public String view(Model model) {
        DashboardSummaryDTO summary = dashboardService.getSummary();
        List<CalendarEventDTO> events = dashboardService.getCalendarEvents();

        model.addAttribute("summary", summary);
        model.addAttribute("events", events);          // ⭐ no JSON string – raw list

        return "dashboard/view";
    }
}
