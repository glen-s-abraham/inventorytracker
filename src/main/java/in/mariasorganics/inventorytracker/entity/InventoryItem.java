package in.mariasorganics.inventorytracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

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

    private String name; // e.g., "Substrate", "Clips", "Covers"

    @Enumerated(EnumType.STRING)
    private UnitOfMeasurement unit; // kg, pcs, meters, etc.

    private double quantity; // current stock

    private double minimumStockLevel; // for low stock alert

    private LocalDate expiryDate; // optional, only for expirable items

    private String remarks;

    /** Optional preferred supplier */
    @ManyToOne
    @JoinColumn(name = "preferred_supplier_id")
    private Supplier preferredSupplier;

    /* --- Derived Properties --- */

    @Transient
    public boolean isExpired() {
        return expiryDate != null && expiryDate.isBefore(LocalDate.now());
    }

    @Transient
    public boolean isLowStock() {
        return quantity <= minimumStockLevel;
    }

    /** true when 0 < daysUntilExpiry ≤ threshold */
    @Transient
    public boolean isExpiringSoon(int thresholdDays) {
        return expiryDate != null &&
               !isExpired() &&
               expiryDate.isBefore(LocalDate.now().plusDays(thresholdDays));
    }
}
