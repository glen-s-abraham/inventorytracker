package in.mariasorganics.inventorytracker.service;

import in.mariasorganics.inventorytracker.dto.RoomMaterialPlanDTO;

import java.util.List;

public interface IOrderPlanningService {
    public List<RoomMaterialPlanDTO> generateRoomWiseOrderPlan();
}
