package in.mariasorganics.inventorytracker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SupplierMappingForm {
    private Long supplierId;
    private int leadTimeInDays;
}
