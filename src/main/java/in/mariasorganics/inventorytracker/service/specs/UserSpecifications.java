package in.mariasorganics.inventorytracker.service.specs;

import org.springframework.data.jpa.domain.Specification;

import in.mariasorganics.inventorytracker.entity.AppUser;

public class UserSpecifications {

    public static Specification<AppUser> hasUsernameLike(String keyword) {
        return (root, query, cb) -> {
            if (keyword == null || keyword.isBlank()) return null;
            return cb.like(cb.lower(root.get("username")), "%" + keyword.toLowerCase() + "%");
        };
    }
}