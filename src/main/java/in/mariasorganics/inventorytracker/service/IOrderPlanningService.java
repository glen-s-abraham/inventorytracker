package in.mariasorganics.inventorytracker.service;

import in.mariasorganics.inventorytracker.dto.OrderPlanEntry;

import java.util.List;

public interface IOrderPlanningService {
    List<OrderPlanEntry> generateOrderPlan();
}
