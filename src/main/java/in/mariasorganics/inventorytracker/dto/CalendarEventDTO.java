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
    private String type;     // e.g., "inoculation-start"
    private String roomName; // e.g., "Tent 1"
    private String color;    // Optional: hex or named color (e.g., "#28a745" or "blue")
}
