package in.mariasorganics.inventorytracker.controller;

import in.mariasorganics.inventorytracker.dto.OrderPlanEntry;
import in.mariasorganics.inventorytracker.service.IOrderPlanningService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/order-planning")
@RequiredArgsConstructor
public class OrderPlanningController {

    private final IOrderPlanningService planningService;

    @GetMapping
    public String showPlan(Model model) {
        List<OrderPlanEntry> planList = planningService.generateOrderPlan();
        model.addAttribute("planList", planList);
        return "order-planning/list";
    }
}
