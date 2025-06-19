package in.mariasorganics.inventorytracker.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "productionCycles")
public class GrowRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private boolean active = true;

    private Integer defaultCycleIntervalDays;

    private String notes;

    @OneToMany(mappedBy = "growRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductionCycle> productionCycles;
}