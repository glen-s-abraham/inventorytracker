package in.mariasorganics.inventorytracker.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GrowRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name; // e.g., Room A, Tent 1

    private boolean active = true;

    private Integer defaultCycleIntervalDays; // e.g., 20 (days between batches)

    private String notes;
}
