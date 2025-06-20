package in.mariasorganics.inventorytracker.dto;

import java.time.LocalDate;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupplierOrderLineDTO {

    private String itemName;
    private String unit;

    private double requiredQty;
    private double availableQty;
    private double shortfall;

    private String supplierName;
    private int    leadTimeDays;
    private LocalDate suggestedOrderDate;   // ← LocalDate, not String!

    /** flag used by Thymeleaf to flip button label / colour */
    private boolean reminderExists;
}
