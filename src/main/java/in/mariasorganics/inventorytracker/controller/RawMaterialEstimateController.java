package in.mariasorganics.inventorytracker.controller;

import in.mariasorganics.inventorytracker.entity.ProductionCycle;
import in.mariasorganics.inventorytracker.entity.RawMaterialEstimate;
import in.mariasorganics.inventorytracker.service.IProductionCycleService;
import in.mariasorganics.inventorytracker.service.IRawMaterialEstimateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/estimates")
public class RawMaterialEstimateController {

    @Autowired
    private IRawMaterialEstimateService estimateService;

    @Autowired
    private IProductionCycleService cycleService;

    // List estimates with optional filters
    @GetMapping
    public String listEstimates(
            @RequestParam(value = "cycleId", required = false) Long cycleId,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {
        Page<RawMaterialEstimate> estimates = estimateService.findAll(keyword, cycleId, PageRequest.of(page, 10));
        model.addAttribute("estimates", estimates);
        model.addAttribute("cycleId", cycleId);
        model.addAttribute("keyword", keyword);
        model.addAttribute("cycles", cycleService.findAll()); // for dropdown
        return "estimates/list";
    }

    // Show form for adding a new estimate
    @GetMapping("/new")
    public String showCreateForm(@RequestParam(required = false) Long cycleId, Model model) {
        RawMaterialEstimate estimate = new RawMaterialEstimate();
        if (cycleId != null) {
            cycleService.findById(cycleId).ifPresent(estimate::setCycle);
        }
        model.addAttribute("estimate", estimate);
        model.addAttribute("cycles", cycleService.findAll());
        return "estimates/form";
    }

    // Show form for editing an existing estimate
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<RawMaterialEstimate> estimateOpt = estimateService.findById(id);
        if (estimateOpt.isEmpty()) {
            return "redirect:/estimates";
        }
        model.addAttribute("estimate", estimateOpt.get());
        model.addAttribute("cycles", cycleService.findAll());
        return "estimates/form";
    }

    // Save new or edited estimate
    @PostMapping("/save")
    public String saveEstimate(@ModelAttribute RawMaterialEstimate estimate) {
        estimateService.save(estimate);
        return "redirect:/estimates";
    }

    // Delete estimate
    @GetMapping("/delete/{id}")
    public String deleteEstimate(@PathVariable Long id) {
        estimateService.deleteById(id);
        return "redirect:/estimates";
    }
}
