package ru.gazizov.webfiend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.gazizov.webfiend.model.Role;
import ru.gazizov.webfiend.model.User;
import ru.gazizov.webfiend.repository.UserRepository;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasAuthority('ADMIN')") //только админы могут постеить эти страницы
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public String userList(Model model){
        model.addAttribute("users", userRepository.findAll());
        return "internal/admin/users";
    }

    @PostMapping("/delete")
    public  String deleteUser(@RequestParam("userId") User user){
        userRepository.delete(user);
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
        user.setUsername(username);
        //получаем в сет стрингов все роли
        Set<String> roles = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());
        user.getRoles().clear();

        for (String key : form.keySet()){
            if (roles.contains(key)){
                user.getRoles().add(Role.valueOf(key));
            }
        }

        userRepository.save(user);
        return "redirect:/admin";
    }
}
