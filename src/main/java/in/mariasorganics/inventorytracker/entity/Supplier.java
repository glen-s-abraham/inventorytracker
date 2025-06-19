package in.mariasorganics.inventorytracker.entity;

import java.util.List;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String contactInfo;

    private int averageLeadTimeInDays;

    private double minimumOrderQuantity;

    private double pricePerUnit;

    private double rating;

    private String remarks;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplierMapping> supplierMappings;
}
