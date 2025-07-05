package in.mariasorganics.inventorytracker.controller;

import in.mariasorganics.inventorytracker.entity.ProductionCycle;
import in.mariasorganics.inventorytracker.service.IGrowRoomService;
import in.mariasorganics.inventorytracker.service.IProductionCycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequestMapping("/cycles")
@RequiredArgsConstructor
public class ProductionCycleController {

    private final IProductionCycleService cycleService;
    private final IGrowRoomService growRoomService;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long growRoomId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inocFrom,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate inocTo,
            @RequestParam(required = false) String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        Page<ProductionCycle> cycles = cycleService.getFilteredPaginated(
                keyword, inocFrom, inocTo, status, growRoomId,
                sortField, sortDir, page, size);

        model.addAttribute("cyclePage", cycles);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("growRoomId", growRoomId);
        model.addAttribute("inocFrom", inocFrom);
        model.addAttribute("inocTo", inocTo);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("growRooms", growRoomService.findAll());

        return "cycles/list";
    }

    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("cycle", new ProductionCycle());
        model.addAttribute("growRooms", growRoomService.findAll());
        return "cycles/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        cycleService.findById(id).ifPresentOrElse(
                cycle -> model.addAttribute("cycle", cycle),
                () -> model.addAttribute("cycle", new ProductionCycle()));
        model.addAttribute("growRooms", growRoomService.findAll());
        return "cycles/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute ProductionCycle cycle,
            @RequestParam(value = "planNext", required = false) boolean planNext) {

        boolean isNewOrActivated = cycle.getId() == null || "ACTIVE".equalsIgnoreCase(cycle.getStatus());

        // Ensure GrowRoom is managed
        if (cycle.getGrowRoom() != null && cycle.getGrowRoom().getId() != null) {
            growRoomService.findById(cycle.getGrowRoom().getId())
                    .ifPresent(cycle::setGrowRoom);
        } else {
            cycle.setGrowRoom(null);
        }

        // Ensure FruitingRoom is managed
        if (cycle.getFruitingRoom() != null && cycle.getFruitingRoom().getId() != null) {
            growRoomService.findById(cycle.getFruitingRoom().getId())
                    .ifPresent(cycle::setFruitingRoom);
        } else {
            cycle.setFruitingRoom(null);
        }

        // Explicitly clear inoculation dates if disabled
        if (Boolean.FALSE.equals(cycle.getHasInoculation())) {
            cycle.setInoculationStartDate(null);
            cycle.setInoculationEndDate(null);
        }

        // Explicitly clear fruiting dates if disabled
        if (Boolean.FALSE.equals(cycle.getHasFruiting())) {
            cycle.setFruitingStartDate(null);
            cycle.setExpectedEndDate(null);
        }

        ProductionCycle saved = cycleService.save(cycle);

        if (isNewOrActivated && planNext) {
            cycleService.planNextCycle(saved);
        }

        return "redirect:/cycles";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        cycleService.deleteById(id);
        return "redirect:/cycles";
    }

    @GetMapping("/clone/{id}")
    public String clone(@PathVariable Long id) {
        cycleService.findById(id).ifPresent(cycleService::planNextCycle);
        return "redirect:/cycles";
    }

}
