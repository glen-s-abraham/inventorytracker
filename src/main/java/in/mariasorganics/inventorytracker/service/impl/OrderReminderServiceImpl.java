package in.mariasorganics.inventorytracker.service.impl;

import in.mariasorganics.inventorytracker.dto.OrderReminderDTO;
import in.mariasorganics.inventorytracker.entity.GrowRoom;
import in.mariasorganics.inventorytracker.entity.OrderReminder;
import in.mariasorganics.inventorytracker.repository.GrowRoomRepository;
import in.mariasorganics.inventorytracker.repository.OrderReminderRepository;
import in.mariasorganics.inventorytracker.service.IOrderReminderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderReminderServiceImpl implements IOrderReminderService {

    private final OrderReminderRepository reminderRepo;
    private final GrowRoomRepository      growRoomRepo;

    @Override
    public void save(OrderReminderDTO dto) {
        GrowRoom room = growRoomRepo.findById(dto.growRoomId())
                                    .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // avoid duplicates
        reminderRepo.findByGrowRoom_IdAndMaterialNameAndSupplierNameAndOrderDate(
                     dto.growRoomId(), dto.materialName(), dto.supplierName(), dto.orderDate())
                    .ifPresent(reminderRepo::delete);

        reminderRepo.save(OrderReminder.builder()
                .growRoom(room)
                .materialName(dto.materialName())
                .supplierName(dto.supplierName())
                .orderDate(dto.orderDate())
                .build());
    }

    @Override
    @Transactional
    public void delete(Long roomId, String mat, String supp, LocalDate date) {
        reminderRepo.deleteByGrowRoom_IdAndMaterialNameAndSupplierNameAndOrderDate(
                roomId, mat, supp, date);
    }
}