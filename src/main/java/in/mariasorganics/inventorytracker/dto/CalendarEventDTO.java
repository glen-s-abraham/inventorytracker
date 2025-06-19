package in.mariasorganics.inventorytracker.dto;

import java.time.LocalDate;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarEventDTO {

    private String     title;
    private LocalDate  date;
    private String     type;
    private String     roomName;
    private String     color;      //  #28a745, red, rgb(…)
}
