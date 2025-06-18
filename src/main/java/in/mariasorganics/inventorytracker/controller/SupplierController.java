package in.mariasorganics.inventorytracker.controller;

import in.mariasorganics.inventorytracker.entity.Supplier;
import in.mariasorganics.inventorytracker.service.ISupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final ISupplierService supplierService;

    /* ---------- LIST & FILTER ------------------------------------------------------- */
    @GetMapping
    public String listSuppliers(
            @RequestParam(value = "keyword",     required = false) String keyword,
            @RequestParam(value = "minRating",   required = false) Double minRating,
            @RequestParam(value = "maxPrice",    required = false) Double maxPrice,
            @RequestParam(value = "maxLeadTime", required = false) Integer maxLeadTime,
            @RequestParam(value = "page",        defaultValue = "0") int page,
            Model model) {

        Page<Supplier> suppliers = supplierService.findAll(
                keyword,
                minRating,
                maxPrice,
                maxLeadTime,
                PageRequest.of(page, 10)
        );

        model.addAttribute("suppliers",    suppliers);
        model.addAttribute("keyword",      keyword);
        model.addAttribute("minRating",    minRating);
        model.addAttribute("maxPrice",     maxPrice);
        model.addAttribute("maxLeadTime",  maxLeadTime);

        return "suppliers/list";
    }

    /* ---------- CREATE / EDIT ------------------------------------------------------- */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("supplier", new Supplier());
        return "suppliers/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("supplier", supplierService.getById(id));
        return "suppliers/form";
    }

    /* ---------- SAVE & DELETE ------------------------------------------------------- */
    @PostMapping("/save")
    public String save(@ModelAttribute Supplier supplier) {
        supplierService.save(supplier);
        return "redirect:/suppliers";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        supplierService.delete(id);
        return "redirect:/suppliers";
    }
}
