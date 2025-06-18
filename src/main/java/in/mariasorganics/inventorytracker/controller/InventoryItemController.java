package in.mariasorganics.inventorytracker.controller;

import in.mariasorganics.inventorytracker.entity.InventoryItem;
import in.mariasorganics.inventorytracker.enums.UnitOfMeasurement;
import in.mariasorganics.inventorytracker.service.IInventoryItemService;
import in.mariasorganics.inventorytracker.service.ISupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryItemController {

    private final IInventoryItemService itemService;
    private final ISupplierService supplierService;

    /* ---------- LIST & FILTER ------------------------------------------------------- */
    @GetMapping
    public String listItems(
            @RequestParam(value = "keyword",   required = false) String keyword,
            @RequestParam(value = "unit",      required = false) UnitOfMeasurement unit,
            @RequestParam(value = "lowStock",  required = false) Boolean lowStockOnly,
            @RequestParam(value = "expired",   required = false) Boolean expiredOnly,
            @RequestParam(value = "expSoon",   required = false) Boolean expiringSoon,
            @RequestParam(value = "threshold", defaultValue = "30") int thresholdDays,
            @RequestParam(value = "supplierId", required = false) Long supplierId,
            @RequestParam(value = "page",      defaultValue = "0") int page,
            Model model) {

        Page<InventoryItem> items = itemService.findAll(
                keyword,
                unit,
                lowStockOnly,
                expiredOnly,
                expiringSoon,
                thresholdDays,
                supplierId,
                PageRequest.of(page, 10));

        model.addAttribute("items", items);
        model.addAttribute("keyword", keyword);
        model.addAttribute("unit", unit);
        model.addAttribute("lowStock", lowStockOnly);
        model.addAttribute("expired", expiredOnly);
        model.addAttribute("expSoon", expiringSoon);
        model.addAttribute("threshold", thresholdDays);
        model.addAttribute("supplierId", supplierId);

        model.addAttribute("unitOptions", UnitOfMeasurement.values());
        model.addAttribute("suppliers", supplierService.findAll()); // for dropdown

        return "inventory/list";
    }

    /* ---------- CREATE / EDIT ------------------------------------------------------- */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("item", new InventoryItem());
        model.addAttribute("unitOptions", UnitOfMeasurement.values());
        model.addAttribute("suppliers", supplierService.findAll());
        return "inventory/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("item", itemService.getById(id));
        model.addAttribute("unitOptions", UnitOfMeasurement.values());
        model.addAttribute("suppliers", supplierService.findAll());
        return "inventory/form";
    }

    /* ---------- SAVE & DELETE ------------------------------------------------------- */
    @PostMapping("/save")
    public String save(@ModelAttribute InventoryItem item) {
        itemService.save(item);
        return "redirect:/inventory";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        itemService.delete(id);
        return "redirect:/inventory";
    }
}
