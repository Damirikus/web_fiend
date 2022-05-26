package ru.gazizov.webfiend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.gazizov.webfiend.model.Role;
import ru.gazizov.webfiend.model.User;
import ru.gazizov.webfiend.service.UserService;

import java.util.Map;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')") //только админы могут постеить эти страницы
public class AdminController {
    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping
    public String userList(Model model){
        model.addAttribute("users", userService.findAll());
        return "internal/admin/users";
    }

    @PostMapping("/delete")
    public  String deleteUser(@RequestParam("userId") User user){
        userService.delete(user);
        return "redirect:/admin";
    }

    @GetMapping("{user}")
    public String userEdit(@PathVariable User user, Model model){
        model.addAttribute("user", user);
        model.addAttribute("roles", Role.values());
        return "internal/admin/user-edit";
    }

    @PostMapping
    public String saveUserEdit(@RequestParam String username,
                               @RequestParam Map<String, String> form, //получаем все данные из формы
                               @RequestParam("userId") User user){ //здесь спринг автоматически по айди найдет юзера

        userService.saveUser(username, form, user);

        return "redirect:/admin";
    }
}
