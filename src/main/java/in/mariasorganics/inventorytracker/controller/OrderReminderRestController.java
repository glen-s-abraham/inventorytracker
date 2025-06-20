// src/main/java/in/mariasorganics/inventorytracker/controller/OrderReminderRestController.java
package in.mariasorganics.inventorytracker.controller;

import in.mariasorganics.inventorytracker.dto.OrderReminderDTO;
import in.mariasorganics.inventorytracker.service.IOrderReminderService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/reminders")
@RequiredArgsConstructor
public class OrderReminderRestController {

    private final IOrderReminderService reminderService;

    /** create (already worked) */
    @PostMapping
    public ResponseEntity<Void> save(@RequestBody OrderReminderDTO dto) {
        reminderService.save(dto);
        return ResponseEntity.ok().build();
    }

    /** 💥 new: delete by composite fields */
    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam Long growRoomId,
                                       @RequestParam String materialName,
                                       @RequestParam String supplierName,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                                               LocalDate orderDate) {

        reminderService.delete(growRoomId, materialName, supplierName, orderDate);
        return ResponseEntity.noContent().build();
    }
}
