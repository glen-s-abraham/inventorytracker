package in.mariasorganics.inventorytracker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RawMaterialEstimate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private ProductionCycle cycle;

    private int numberOfBags;

    private double spawnPerBag;      // in kg
    private double substratePerBag;  // in kg
    private int packagingPerBag;     // in count (e.g., covers, clips, etc.)

    private double totalSpawn;
    private double totalSubstrate;
    private int totalPackaging;

    private String remarks;
}
