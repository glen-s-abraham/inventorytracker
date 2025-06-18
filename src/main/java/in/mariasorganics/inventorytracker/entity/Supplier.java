package in.mariasorganics.inventorytracker.entity;

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

    private double rating; // e.g., 4.5

    private String remarks;
}
