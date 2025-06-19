package in.mariasorganics.inventorytracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "growRoom")
public class ProductionCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grow_room_id", nullable = false)
    private GrowRoom growRoom;

    private String code;

    private LocalDate inoculationStartDate;
    private LocalDate inoculationEndDate;
    private LocalDate fruitingStartDate;
    private LocalDate expectedEndDate;

    private String status;

    private String remarks;

    @OneToOne(mappedBy = "cycle", cascade = CascadeType.ALL, orphanRemoval = true)
    private RawMaterialEstimate estimate;
}
