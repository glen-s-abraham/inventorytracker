package in.mariasorganics.inventorytracker.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import in.mariasorganics.inventorytracker.entity.AppUser;
import in.mariasorganics.inventorytracker.service.IUserService;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping
    public String list(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "10") int size,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String sortField,
                       @RequestParam(required = false, defaultValue = "desc") String sortDir,
                       Model model) {
        Page<AppUser> usersPage = userService.getPaginated(page, size, keyword, sortField, sortDir);
        model.addAttribute("usersPage", usersPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        return "users/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("user", new AppUser());
        return "users/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("user", userService.getById(id));
        return "users/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute AppUser user) {
        userService.save(user);
        return "redirect:/users";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/users";
    }
}