package in.mariasorganics.inventorytracker.controller;

import in.mariasorganics.inventorytracker.entity.InventoryItem;
import in.mariasorganics.inventorytracker.entity.Supplier;
import in.mariasorganics.inventorytracker.entity.SupplierMapping;
import in.mariasorganics.inventorytracker.entity.SupplierMappingId;
import in.mariasorganics.inventorytracker.enums.UnitOfMeasurement;
import in.mariasorganics.inventorytracker.repository.SupplierMappingRepository;
import in.mariasorganics.inventorytracker.service.IInventoryItemService;
import in.mariasorganics.inventorytracker.service.ISupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Inventory CRUD & filtering controller
 * – supports optional multi‑supplier mapping for each item.
 */
@Controller
@RequestMapping("/inventory")
@RequiredArgsConstructor
public class InventoryItemController {

    private final IInventoryItemService itemService;
    private final ISupplierService      supplierService;
    private final SupplierMappingRepository supplierMappingRepository;

    /* ---------- LIST & FILTER ------------------------------------------------ */
    @GetMapping
    public String listItems(@RequestParam(value = "keyword",      required = false) String keyword,
                            @RequestParam(value = "unit",         required = false) UnitOfMeasurement unit,
                            @RequestParam(value = "lowStock",     required = false) Boolean lowStockOnly,
                            @RequestParam(value = "expired",      required = false) Boolean expiredOnly,
                            @RequestParam(value = "expSoon",      required = false) Boolean expiringSoon,
                            @RequestParam(value = "threshold",    defaultValue = "30") int thresholdDays,
                            @RequestParam(value = "supplierId",   required = false) Long supplierId,
                            @RequestParam(value = "page",         defaultValue = "0")  int page,
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
        item.setSupplierMappings(new ArrayList<>());   // avoid null checks in template
        model.addAttribute("item",         item);
        model.addAttribute("unitOptions",  UnitOfMeasurement.values());
        model.addAttribute("suppliers",    supplierService.findAll());
        return "inventory/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        InventoryItem item = itemService.getById(id);
        model.addAttribute("item",         item);
        model.addAttribute("unitOptions",  UnitOfMeasurement.values());
        model.addAttribute("suppliers",    supplierService.findAll());
        return "inventory/form";
    }

    /* ---------- SAVE --------------------------------------------------------- */
    @PostMapping("/save")
    @Transactional
    public String save(@ModelAttribute InventoryItem item,
                       @RequestParam(value = "supplierIds", required = false) List<Long> supplierIds) {

        // 1️⃣ Save / update the item first (generates ID for new rows)
        InventoryItem savedItem = itemService.save(item);

        // 2️⃣ Clean old mappings to avoid duplicates (only matters on edit)
        supplierMappingRepository.deleteByItem(savedItem);

        // 3️⃣ Persist the newly‑selected suppliers
        if (supplierIds != null) {
            supplierIds.stream().distinct().forEach(supId -> {
                Supplier supplier = supplierService.getById(supId);
                SupplierMappingId mappingId = new SupplierMappingId(savedItem.getId(), supplier.getId());
                SupplierMapping mapping = SupplierMapping.builder()
                        .id(mappingId)
                        .item(savedItem)
                        .supplier(supplier)
                        .leadTimeInDays(0) // default; adjust elsewhere if needed
                        .preferred(false)
                        .build();
                supplierMappingRepository.save(mapping);
            });
        }

        return "redirect:/inventory";
    }

    /* ---------- DELETE ------------------------------------------------------- */
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        itemService.delete(id);
        return "redirect:/inventory";
    }
}
