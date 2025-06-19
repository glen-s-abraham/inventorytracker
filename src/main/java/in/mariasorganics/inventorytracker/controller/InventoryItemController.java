package in.mariasorganics.inventorytracker.controller;

import in.mariasorganics.inventorytracker.entity.InventoryItem;
import in.mariasorganics.inventorytracker.entity.Supplier;
import in.mariasorganics.inventorytracker.entity.SupplierMapping;
import in.mariasorganics.inventorytracker.entity.SupplierMappingId;
import in.mariasorganics.inventorytracker.enums.UnitOfMeasurement;
import in.mariasorganics.inventorytracker.service.IInventoryItemService;
import in.mariasorganics.inventorytracker.service.ISupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Inventory CRUD & filtering controller
 * – allows linking **multiple suppliers** to an item without hitting the
 * “duplicate identifier” error in Hibernate.
 */
@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryItemController {

    private final IInventoryItemService itemService;
    private final ISupplierService      supplierService;

    /* ---------- LIST & FILTER ------------------------------------------------ */
    @GetMapping
    public String listItems(@RequestParam(value = "keyword",    required = false) String keyword,
                            @RequestParam(value = "unit",       required = false) UnitOfMeasurement unit,
                            @RequestParam(value = "lowStock",   required = false) Boolean lowStockOnly,
                            @RequestParam(value = "expired",    required = false) Boolean expiredOnly,
                            @RequestParam(value = "expSoon",    required = false) Boolean expiringSoon,
                            @RequestParam(value = "threshold",  defaultValue = "30") int thresholdDays,
                            @RequestParam(value = "supplierId", required = false) Long supplierId,
                            @RequestParam(value = "page",       defaultValue = "0") int page,
                            Model model) {

        Page<InventoryItem> items = itemService.findAll(
                keyword, unit, lowStockOnly, expiredOnly, expiringSoon,
                thresholdDays, supplierId, PageRequest.of(page, 10));

        model.addAttribute("items",       items);
        model.addAttribute("keyword",     keyword);
        model.addAttribute("unit",        unit);
        model.addAttribute("lowStock",    lowStockOnly);
        model.addAttribute("expired",     expiredOnly);
        model.addAttribute("expSoon",     expiringSoon);
        model.addAttribute("threshold",   thresholdDays);
        model.addAttribute("supplierId",  supplierId);
        model.addAttribute("unitOptions", UnitOfMeasurement.values());
        model.addAttribute("suppliers",   supplierService.findAll());
        return "inventory/list";
    }

    /* ---------- CREATE / EDIT ------------------------------------------------ */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        InventoryItem item = new InventoryItem();
        item.setSupplierMappings(new ArrayList<>()); // avoid null checks in the view
        commonFormModel(model, item);
        return "inventory/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        InventoryItem item = itemService.getById(id);
        commonFormModel(model, item);
        return "inventory/form";
    }

    private void commonFormModel(Model model, InventoryItem item){
        model.addAttribute("item", item);
        model.addAttribute("unitOptions", UnitOfMeasurement.values());
        model.addAttribute("suppliers", supplierService.findAll());
    }

    /* ---------- SAVE --------------------------------------------------------- */
    @PostMapping("/save")
    @Transactional
    public String save(@Valid @ModelAttribute InventoryItem form,
                       @RequestParam(value = "supplierIds", required = false) List<Long> supplierIds) {

        // ✨ 1) Load managed entity (if exists) so we work in SAME Hibernate session
        InventoryItem managed;
        if (form.getId() != null) {
            managed = itemService.getById(form.getId());
            copyScalarFields(form, managed);
        } else {
            managed = itemService.save(form); // generates ID for brand‑new item
        }

        // ✨ 2) Prepare selected supplier IDs (may be empty)
        List<Long> selected = supplierIds == null ? List.of()
                : supplierIds.stream().distinct().collect(Collectors.toList());

        // Ensure list exists
        if (managed.getSupplierMappings() == null) {
            managed.setSupplierMappings(new ArrayList<>());
        }

        /*
         * ✨ 3) REMOVE mappings that user un‑ticked
         *     Hibernate will delete them thanks to orphanRemoval=true on the entity
         */
        managed.getSupplierMappings().removeIf(m -> !selected.contains(m.getSupplier().getId()));

        // ✨ 4) ADD new mappings that don’t yet exist
        List<Long> existing = managed.getSupplierMappings().stream()
                .map(m -> m.getSupplier().getId()).toList();

        for (Long supId : selected) {
            if (!existing.contains(supId)) {
                Supplier supplier = supplierService.getById(supId);
                SupplierMapping mapping = SupplierMapping.builder()
                        .id(new SupplierMappingId(managed.getId(), supplier.getId()))
                        .item(managed)
                        .supplier(supplier)
                        .leadTimeInDays(0)
                        .preferred(false)
                        .build();
                managed.getSupplierMappings().add(mapping);
            }
        }

        // No explicit save needed – dirty checking flushes on TX commit
        return "redirect:/inventory";
    }

    private void copyScalarFields(InventoryItem src, InventoryItem dst){
        dst.setName(src.getName());
        dst.setUnit(src.getUnit());
        dst.setQuantity(src.getQuantity());
        dst.setMinimumStockLevel(src.getMinimumStockLevel());
        dst.setExpiryDate(src.getExpiryDate());
        dst.setRemarks(src.getRemarks());
    }

    /* ---------- DELETE ------------------------------------------------------- */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        itemService.delete(id);
        return "redirect:/inventory";
    }
}
