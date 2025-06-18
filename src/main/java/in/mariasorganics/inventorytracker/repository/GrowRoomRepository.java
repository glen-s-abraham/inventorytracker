package in.mariasorganics.inventorytracker.repository;

import in.mariasorganics.inventorytracker.entity.GrowRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface GrowRoomRepository
        extends JpaRepository<GrowRoom, Long>, JpaSpecificationExecutor<GrowRoom> {

    List<GrowRoom> findByActiveTrueOrderByNameAsc();
}