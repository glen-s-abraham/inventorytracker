package in.mariasorganics.inventorytracker.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventDTO {
    private String title;
    private LocalDate date;
    private String type; // e.g., "Inoculation", "Order", etc.
    private String roomName;
}