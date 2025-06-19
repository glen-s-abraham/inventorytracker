package in.mariasorganics.inventorytracker.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryDTO {

    /** active grow‑rooms */
    private int totalRoomCount;

    /** cycles whose status = “ACTIVE” */
    private int activeCycleCount;

    /** inventory items below the alert threshold */
    private int lowInventoryCount;
}
