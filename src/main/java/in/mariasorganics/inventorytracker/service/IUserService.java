package in.mariasorganics.inventorytracker.service;

import org.springframework.data.domain.Page;

import in.mariasorganics.inventorytracker.entity.AppUser;

public interface IUserService {
    Page<AppUser> getPaginated(int page, int size, String keyword, String sortField, String sortDir);

    AppUser getById(Long id);

    AppUser save(AppUser user);

    void deleteById(Long id);
}