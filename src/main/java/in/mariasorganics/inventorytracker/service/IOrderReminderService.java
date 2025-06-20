package in.mariasorganics.inventorytracker.service;

import in.mariasorganics.inventorytracker.dto.OrderReminderDTO;

import java.time.LocalDate;
import java.util.List;

// src/main/java/in/mariasorganics/inventorytracker/service/IOrderReminderService.java
public interface IOrderReminderService {
    void save(OrderReminderDTO dto);
    void delete(Long roomId, String mat, String supp, LocalDate date);
}
