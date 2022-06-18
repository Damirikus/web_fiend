package ru.gazizov.webfiend.controller;

import org.hibernate.Hibernate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.gazizov.webfiend.model.Role;
import ru.gazizov.webfiend.model.User;
import ru.gazizov.webfiend.service.UserService;

import javax.validation.Valid;
import java.util.Map;

@Controller
@RequestMapping("/profile")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("{user}")
    public String profile(@AuthenticationPrincipal User userCurrent,
                          Model model,
                          @PathVariable User user
    ){

        model.addAttribute("user", user);

        if (userCurrent.equals(user)){
            model.addAttribute("currentUser", true);
        }
        model.addAttribute("isSubscriber", user.getSubscribers().contains(userCurrent));
        model.addAttribute("subscribersCount", user.getSubscribers().size());
        model.addAttribute("subscriptionsCount", user.getSubscriptions().size());
        return "internal/profile";
    }

    @GetMapping("{userId}/edit")
    public String profileEdit(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("user", user);
        return "internal/profile-edit";
    }

    @PostMapping("{user}/edit")
    public String saveProfileChanges(@AuthenticationPrincipal User userExists,
                                     @RequestParam("password2") String password2,
                                     @Valid User user,
                                     BindingResult bindingResult,
                                     Model model){

        if (user.getPassword() != null && !user.getPassword().equals(password2)){
            model.addAttribute("passwordError", "Passwords are not equals!");
            return "internal/profile-edit";
        }

        if (!user.getRoles().contains(Role.ADMIN)){
            if ((bindingResult.hasErrors() && !user.getPassword().isEmpty())){
                Map<String, String> errors = ControllersUtils.getErrors(bindingResult);
                model.mergeAttributes(errors);
                return "internal/profile-edit";
            }
        }


        userService.updateProfile(userExists, user.getEmail(), user.getPassword());
        return "redirect:/profile/{user}";
    }


    @GetMapping("{user}/subscribe")
    @Transactional
    public String subscribe(@AuthenticationPrincipal User currentUser,
                            @PathVariable User user){

        currentUser = userService.findById(currentUser.getId());
        userService.subscribe(user, currentUser);
        return "redirect:/profile/{user}";
    }

    @GetMapping("{user}/unsubscribe")
    public String unsubscribe(@AuthenticationPrincipal User currentUser,
                            @PathVariable User user){
        userService.unSubscribe(user, currentUser);
        return "redirect:/profile/{user}";
    }


    @GetMapping("{user}/{type}/list")
    public String subscriptionsList(@PathVariable User user,
                                    @PathVariable String type,
                                    Model model){
        model.addAttribute("userCurrent", user);
        if (type.equals("subscriptions")){
            model.addAttribute("users", user.getSubscriptions());
        } else {
            model.addAttribute("users", user.getSubscribers());
        }

        return "internal/subscriptions";
    }
}
