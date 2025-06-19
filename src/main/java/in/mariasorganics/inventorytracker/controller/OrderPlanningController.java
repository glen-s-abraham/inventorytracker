package in.mariasorganics.inventorytracker.controller;

import in.mariasorganics.inventorytracker.dto.RoomMaterialPlanDTO;
import in.mariasorganics.inventorytracker.service.IOrderPlanningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Displays the room‑wise order‑planning view.
 */
@Controller
@RequestMapping("/order-planning")
@RequiredArgsConstructor
public class OrderPlanningController {

    private final IOrderPlanningService planningService;

    @GetMapping
    public String showPlan(Model model) {

        // pull the room‑centric purchase recommendations
        List<RoomMaterialPlanDTO> roomPlans = planningService.generateRoomWiseOrderPlan();

        model.addAttribute("roomPlans", roomPlans);
        return "order-planning/list";   // remember to update the list.html to iterate over roomPlans
    }
}
