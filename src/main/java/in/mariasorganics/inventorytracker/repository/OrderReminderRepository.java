// src/main/java/in/mariasorganics/inventorytracker/repository/OrderReminderRepository.java
package in.mariasorganics.inventorytracker.repository;

import in.mariasorganics.inventorytracker.entity.OrderReminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface OrderReminderRepository extends JpaRepository<OrderReminder, Long> {

        /* for duplicate‑check & delete */
        Optional<OrderReminder> findByGrowRoom_IdAndMaterialNameAndSupplierNameAndOrderDate(
                        Long growRoomId, String materialName, String supplierName, LocalDate orderDate);

        void deleteByGrowRoom_IdAndMaterialNameAndSupplierNameAndOrderDate(
                        Long growRoomId, String materialName, String supplierName, LocalDate orderDate);

        boolean existsByGrowRoom_IdAndMaterialNameAndSupplierNameAndOrderDate(
                        Long growRoomId, String materialName, String supplierName, LocalDate orderDate);

        List<OrderReminder> findByOrderDateBetween(LocalDate start, LocalDate end);

}
