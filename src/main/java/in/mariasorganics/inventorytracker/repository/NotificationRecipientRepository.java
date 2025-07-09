package in.mariasorganics.inventorytracker.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import in.mariasorganics.inventorytracker.entity.NotificationRecipient;

public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Long> {
    List<NotificationRecipient> findByActiveTrue();
}
