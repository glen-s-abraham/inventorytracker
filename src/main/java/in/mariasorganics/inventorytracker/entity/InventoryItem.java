package in.mariasorganics.inventorytracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

import in.mariasorganics.inventorytracker.enums.UnitOfMeasurement;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private UnitOfMeasurement unit;

    private double quantity;

    private double minimumStockLevel;

    private LocalDate expiryDate;

    private String remarks;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplierMapping> supplierMappings;

    @Transient
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    @Transient
    public boolean isLowStock() {
        return quantity <= minimumStockLevel;
    }

    @Transient
    public boolean isExpiringSoon(int thresholdDays) {
        return expiryDate != null && !isExpired() && expiryDate.isBefore(LocalDate.now().plusDays(thresholdDays));
    }
}
