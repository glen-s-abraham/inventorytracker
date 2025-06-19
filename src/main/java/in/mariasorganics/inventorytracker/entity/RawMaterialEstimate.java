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

    @OneToOne(optional = false)
    @JoinColumn(name = "cycle_id")
    private ProductionCycle cycle;

    private int numberOfBags;

    private double spawnPerBag;
    private double substratePerBag;
    private int packagingPerBag;

    private double totalSpawn;
    private double totalSubstrate;
    private int totalPackaging;

    private String remarks;
}