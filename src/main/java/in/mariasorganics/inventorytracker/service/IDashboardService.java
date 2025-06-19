package in.mariasorganics.inventorytracker.service;

import java.util.List;

import in.mariasorganics.inventorytracker.dto.CalendarEventDTO;
import in.mariasorganics.inventorytracker.dto.DashboardSummaryDTO;

public interface IDashboardService {
    public DashboardSummaryDTO getSummary();

    public List<CalendarEventDTO> getCalendarEvents();
}