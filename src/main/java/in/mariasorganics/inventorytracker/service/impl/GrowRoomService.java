package in.mariasorganics.inventorytracker.service.impl;

import in.mariasorganics.inventorytracker.entity.GrowRoom;
import in.mariasorganics.inventorytracker.repository.GrowRoomRepository;
import in.mariasorganics.inventorytracker.service.IGrowRoomService;
import in.mariasorganics.inventorytracker.service.specs.GrowRoomSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GrowRoomService implements IGrowRoomService {

    private final GrowRoomRepository repo;

    @Override
    public List<GrowRoom> findAll() {
        return repo.findAll(Sort.by("name"));
    }

    @Override
    public Optional<GrowRoom> findById(Long id) {
        return repo.findById(id);
    }

    @Override
    public GrowRoom save(GrowRoom growRoom) {
        return repo.save(growRoom);
    }

    @Override
    public void deleteById(Long id) {
        repo.deleteById(id);
    }

    @Override
    public Page<GrowRoom> getPaginated(int page, int size) {
        return repo.findAll(PageRequest.of(page, size, Sort.by("name")));
    }

    @Override
    public Page<GrowRoom> getFilteredPaginated(String keyword,
                                               Boolean active,
                                               String sortField,
                                               String sortDir,
                                               int page,
                                               int size) {

        Specification<GrowRoom> spec = Specification
                .where(GrowRoomSpecifications.hasKeyword(keyword))
                .and(GrowRoomSpecifications.isActive(active));

        Sort sort = Sort.by(sortField != null ? sortField : "name");
        sort = "asc".equalsIgnoreCase(sortDir) ? sort.ascending() : sort.descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        return repo.findAll(spec, pageable);
    }
}