package in.mariasorganics.inventorytracker.controller;

import in.mariasorganics.inventorytracker.entity.GrowRoom;
import in.mariasorganics.inventorytracker.service.IGrowRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/grow-rooms")
@RequiredArgsConstructor
public class GrowRoomController {

    private final IGrowRoomService service;

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean active,
            @RequestParam(required = false) String sortField,
            @RequestParam(defaultValue = "desc") String sortDir,
            Model model) {

        Page<GrowRoom> growRoomPage = service.getFilteredPaginated(
                keyword, active, sortField, sortDir, page, size);

        model.addAttribute("growRoomPage", growRoomPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("active", active);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        return "growrooms/list";
    }

    @GetMapping("/new")
    public String form(Model model) {
        model.addAttribute("growRoom", new GrowRoom());
        return "growrooms/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        service.findById(id).ifPresentOrElse(
                room -> model.addAttribute("growRoom", room),
                () -> model.addAttribute("growRoom", new GrowRoom())
        );
        return "growrooms/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute GrowRoom growRoom) {
        service.save(growRoom);
        return "redirect:/grow-rooms";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/grow-rooms";
    }
}
