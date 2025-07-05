package in.mariasorganics.inventorytracker.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"growRoom", "fruitingRoom"})
public class ProductionCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grow_room_id", nullable = false)
    private GrowRoom growRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fruiting_room_id")
    private GrowRoom fruitingRoom;

    private String code;

    private LocalDate inoculationStartDate;
    private LocalDate inoculationEndDate;

    private LocalDate fruitingStartDate;
    private LocalDate expectedEndDate;

    private String status;

    private String remarks;

    private Boolean hasInoculation = true;
    private Boolean hasFruiting = true;

    @OneToOne(mappedBy = "cycle", cascade = CascadeType.ALL, orphanRemoval = true)
    private RawMaterialEstimate estimate;
}

