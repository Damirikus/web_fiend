package ru.gazizov.webfiend.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.gazizov.webfiend.model.Message;
import ru.gazizov.webfiend.model.Role;
import ru.gazizov.webfiend.model.User;
import ru.gazizov.webfiend.repository.MessageRepository;
import ru.gazizov.webfiend.repository.UserRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Controller
public class MainController {

    @Value("${upload.path}") //из проперти берем пав
    private String path;

    //через конструктор автоматически идет внедрение зависимости
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    public MainController(MessageRepository messageRepository, UserRepository userRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
    }


    @GetMapping
    public String mainPage() {
        // здесь проверяю залогинен ли пользователь
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        System.out.println(authentication.getName());
//        if (authentication.getName().isEmpty()) {
//            System.out.println("HereeHERERREREREREREREERRERE");
            return "main";
//        }
//        System.out.println(authentication.isAuthenticated());
//
//        return "redirect:/messages";
    }

    @GetMapping("/messages")
    public String messages(Model model) {
        Iterable<Message> messages = messageRepository.findAll();
        model.addAttribute("messages", messages);
        return "messages";
    }

    @PostMapping("/messages")
    public String addMessage(@RequestParam(defaultValue = "tag", required = false) String tag,
                             @RequestParam String text,
                             @AuthenticationPrincipal User user,
                             @RequestParam("file") MultipartFile file) throws IOException {
        if (!text.isEmpty()){
            Message message = new Message(new Date(), tag, text, user);

            if (file != null && !file.getOriginalFilename().isEmpty()){

                String uuidFile = UUID.randomUUID().toString(); // создаем рандомное имя файлу
                String result = uuidFile + "." + file.getOriginalFilename(); //собираем полное имя
                message.setFilename(result); // устанавливаем имя файла в поле мессаджа

                file.transferTo(new File(path + "/" + result)); //загружаем файл
            }

            messageRepository.save(message);

        }
        return "redirect:/messages";
    }

    @GetMapping("/registration")
    public String register(){
        return "registration";
    }

    @PostMapping("/registration")
    public String registerPost(User user, Model model){
        User userByDb = userRepository.findByUsername(user.getUsername());
        if (userByDb != null){
            model.addAttribute("message", "User already exists!");
            return "registration";
        }
        if (user.getPassword().isEmpty()) {
            model.addAttribute("message", "Please write the password!");
            return "registration";
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        userRepository.save(user);
        return "redirect:/login";
    }

}
