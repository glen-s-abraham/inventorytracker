package in.mariasorganics.inventorytracker.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderPlanEntry {

    private String itemName;
    private String unit;
    private double totalRequired;
    private double availableStock;
    private double quantityToOrder;
    private String supplierName;
    private int leadTimeDays;
    private String suggestedOrderDate;
}
