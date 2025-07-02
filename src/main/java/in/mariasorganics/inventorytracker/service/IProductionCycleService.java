package in.mariasorganics.inventorytracker.service;

import in.mariasorganics.inventorytracker.entity.ProductionCycle;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IProductionCycleService {

    List<ProductionCycle> findAll();

    Optional<ProductionCycle> findById(Long id);

    ProductionCycle save(ProductionCycle cycle);

    void deleteById(Long id);

    Page<ProductionCycle> getPaginated(int page, int size);

    Page<ProductionCycle> getFilteredPaginated(String keyword,
                                               LocalDate startFrom,
                                               LocalDate startTo,
                                               String status,
                                               Long growRoomId,
                                               String sortField,
                                               String sortDir,
                                               int page,
                                               int size);
    
    public void planNextCycle(ProductionCycle current);
}