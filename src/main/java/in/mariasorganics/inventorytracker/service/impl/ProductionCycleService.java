package in.mariasorganics.inventorytracker.service.impl;

import in.mariasorganics.inventorytracker.entity.GrowRoom;
import in.mariasorganics.inventorytracker.entity.ProductionCycle;
import in.mariasorganics.inventorytracker.repository.GrowRoomRepository;
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
    private final GrowRoomRepository growRoomRepo;

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
        // Ensure GrowRoom and FruitingRoom are managed entities
        if (cycle.getGrowRoom() != null && cycle.getGrowRoom().getId() != null) {
            GrowRoom gr = growRoomRepo.findById(cycle.getGrowRoom().getId()).orElseThrow();
            cycle.setGrowRoom(gr);
        }
        if (cycle.getFruitingRoom() != null && cycle.getFruitingRoom().getId() != null) {
            GrowRoom fr = growRoomRepo.findById(cycle.getFruitingRoom().getId()).orElse(null);
            cycle.setFruitingRoom(fr);
        }
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
        ProductionCycle next = new ProductionCycle();
        next.setCode(current.getCode() + "_NEXT");

        // Ensure managed references
        next.setGrowRoom(growRoomRepo.findById(current.getGrowRoom().getId()).orElseThrow());
        if (current.getFruitingRoom() != null) {
            next.setFruitingRoom(growRoomRepo.findById(current.getFruitingRoom().getId()).orElse(null));
        }

        next.setHasInoculation(current.getHasInoculation());
        next.setHasFruiting(current.getHasFruiting());
        next.setStatus("PLANNED");
        next.setRemarks("Auto‑generated from " + current.getCode());

        // Determine base start date for next cycle
        LocalDate baseStart = null;

        if (Boolean.TRUE.equals(current.getHasFruiting()) && current.getExpectedEndDate() != null) {
            baseStart = current.getExpectedEndDate();
        } else if (Boolean.TRUE.equals(current.getHasInoculation()) && current.getInoculationEndDate() != null) {
            baseStart = current.getInoculationEndDate();
        }

        // If no base start, cannot clone dates
        if (baseStart == null) {
            repo.save(next);
            return;
        }

        // Clone inoculation dates
        if (Boolean.TRUE.equals(current.getHasInoculation())
                && current.getInoculationStartDate() != null
                && current.getInoculationEndDate() != null) {

            long inocDays = ChronoUnit.DAYS.between(current.getInoculationStartDate(), current.getInoculationEndDate());
            LocalDate inocStart = baseStart;
            LocalDate inocEnd = inocStart.plusDays(inocDays);

            next.setInoculationStartDate(inocStart);
            next.setInoculationEndDate(inocEnd);

            if (!Boolean.TRUE.equals(current.getHasFruiting())) {
                next.setExpectedEndDate(inocEnd);
            }
        }

        // Clone fruiting dates
        if (Boolean.TRUE.equals(current.getHasFruiting())
                && current.getFruitingStartDate() != null
                && current.getExpectedEndDate() != null) {

            long fruitDays = ChronoUnit.DAYS.between(current.getFruitingStartDate(), current.getExpectedEndDate());
            LocalDate fruitStart = Boolean.TRUE.equals(current.getHasInoculation()) && next.getInoculationEndDate() != null
                    ? next.getInoculationEndDate()
                    : baseStart;

            LocalDate fruitEnd = fruitStart.plusDays(fruitDays);
            next.setFruitingStartDate(fruitStart);
            next.setExpectedEndDate(fruitEnd);
        }

        repo.save(next);
    }
}
