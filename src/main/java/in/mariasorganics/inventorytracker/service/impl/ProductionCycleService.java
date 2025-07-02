package in.mariasorganics.inventorytracker.service.impl;

import in.mariasorganics.inventorytracker.entity.ProductionCycle;
import in.mariasorganics.inventorytracker.repository.ProductionCycleRepository;
import in.mariasorganics.inventorytracker.service.IProductionCycleService;
import in.mariasorganics.inventorytracker.service.specs.ProductionCycleSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductionCycleService implements IProductionCycleService {

    private final ProductionCycleRepository repo;

    @Override
    public List<ProductionCycle> findAll() {
        return repo.findAll(Sort.by("inoculationStartDate").descending());
    }

    @Override
    public Optional<ProductionCycle> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public ProductionCycle save(ProductionCycle cycle) {
        return repo.save(cycle);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public Page<ProductionCycle> getPaginated(int page, int size) {
        return repo.findAll(PageRequest.of(page, size, Sort.by("inoculationStartDate").descending()));
    }

    @Override
    public Page<ProductionCycle> getFilteredPaginated(String keyword,
                                                      LocalDate startFrom,
                                                      LocalDate startTo,
                                                      String status,
                                                      Long growRoomId,
                                                      String sortField,
                                                      String sortDir,
                                                      int page,
                                                      int size) {

        Specification<ProductionCycle> spec = Specification
                .where(ProductionCycleSpecifications.hasKeyword(keyword))
                .and(ProductionCycleSpecifications.inoculationStartBetween(startFrom, startTo))
                .and(ProductionCycleSpecifications.statusIs(status))
                .and(ProductionCycleSpecifications.growRoomIdIs(growRoomId));

        Sort sort = Sort.by(sortField != null ? sortField : "inoculationStartDate");
        sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return repo.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public void planNextCycle(ProductionCycle current) {
        // 1. Derive dates
        LocalDate inocStart = current.getExpectedEndDate();
        int inocDays = (int) ChronoUnit.DAYS.between(current.getInoculationStartDate(), current.getInoculationEndDate());
        int fruitDays = (int) ChronoUnit.DAYS.between(current.getFruitingStartDate(), current.getExpectedEndDate());

        LocalDate inocEnd = inocStart.plusDays(inocDays);
        LocalDate fruitEnd = inocEnd.plusDays(fruitDays);

        // 2. Build a new entity (PLANNED)
        ProductionCycle next = new ProductionCycle();
        next.setCode(current.getCode() + "_NEXT");
        next.setGrowRoom(current.getGrowRoom());
        next.setInoculationStartDate(inocStart);
        next.setInoculationEndDate(inocEnd);
        next.setFruitingStartDate(inocEnd);
        next.setExpectedEndDate(fruitEnd);
        next.setStatus("PLANNED");
        next.setRemarks("Auto‑generated from " + current.getCode());

        repo.save(next);
    }
} 
