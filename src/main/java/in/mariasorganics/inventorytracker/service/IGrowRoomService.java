package in.mariasorganics.inventorytracker.service;

import in.mariasorganics.inventorytracker.entity.GrowRoom;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface IGrowRoomService {

    List<GrowRoom> findAll();

    Optional<GrowRoom> findById(Long id);

    GrowRoom save(GrowRoom growRoom);

    void deleteById(Long id);

    Page<GrowRoom> getPaginated(int page, int size);

    Page<GrowRoom> getFilteredPaginated(String keyword,
                                        Boolean active,
                                        String sortField,
                                        String sortDir,
                                        int page,
                                        int size);
}