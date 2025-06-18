package in.mariasorganics.inventorytracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductionCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grow_room_id", nullable = false)
    private GrowRoom growRoom;

    private String code;

    private LocalDate inoculationStartDate; // 👈 Phase 1: Inoculation starts
    private LocalDate inoculationEndDate; // 👈 Phase 1: Inoculation completes

    private LocalDate fruitingStartDate; // 👈 Phase 2: Fruiting begins
    private LocalDate expectedEndDate; // 👈 Overall cycle end

    private String status; // PLANNED / ACTIVE / COMPLETED / CANCELLED

    private String remarks;
}
