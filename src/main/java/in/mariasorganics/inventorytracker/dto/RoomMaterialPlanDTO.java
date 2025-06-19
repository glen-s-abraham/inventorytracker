package in.mariasorganics.inventorytracker.dto;

import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMaterialPlanDTO {
    private Long roomId;
    private String roomName;
    private LocalDate nextInoculationIdealDate;
    private List<SupplierOrderLineDTO> orderLines;
}
