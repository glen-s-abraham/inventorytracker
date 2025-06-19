package in.mariasorganics.inventorytracker.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierOrderLineDTO {
    private String itemName;
    private String unit;
    private double requiredQty;
    private double availableQty;
    private double shortfall;
    private String supplierName;
    private int leadTimeDays;
    private String suggestedOrderDate;
}
