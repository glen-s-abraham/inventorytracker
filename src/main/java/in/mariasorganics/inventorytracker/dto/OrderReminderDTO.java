package in.mariasorganics.inventorytracker.dto;

import java.time.LocalDate;

public record OrderReminderDTO(
        Long id,
        Long growRoomId,
        String materialName,
        String supplierName,
        LocalDate orderDate
) {}