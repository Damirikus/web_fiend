package ru.gazizov.webfiend.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.gazizov.webfiend.model.Message;
import ru.gazizov.webfiend.model.User;
import ru.gazizov.webfiend.repository.MessageRepository;
import ru.gazizov.webfiend.service.UserService;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.UUID;

@Controller
public class MainController {

    @Value("${upload.path}") //из проперти берем пав
    private String path;

    //через конструктор автоматически идет внедрение зависимости
    private final MessageRepository messageRepository;
    private final UserService userService;

    public MainController(MessageRepository messageRepository, UserService userService) {
        this.messageRepository = messageRepository;
        this.userService = userService;
    }

    @GetMapping
    public String mainPage(Model model) {
            return "main";
    }

    @GetMapping("/messages")
    public String messages(Model model) {
        Iterable<Message> messages = messageRepository.findAll();
        model.addAttribute("messages", messages);
        return "internal/messages";
    }

    @PostMapping("/messages")
    public String addMessage(@RequestParam(defaultValue = "tag", required = false) String tag,
                             @RequestParam String text,
                             @AuthenticationPrincipal User user,
                             @RequestParam("file") MultipartFile file) throws IOException {
        if (!text.isEmpty()){
            Message message = new Message(LocalDateTime.now(), tag, text, user);

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

        String command = userService.addUser(user);
        if (!command.equals("yes")){
            model.addAttribute("message", command);
            return "registration";
        }
        return "redirect:/login";
    }

    //контроллер для обработки ссылки с почты
    @GetMapping("/activate/{code}")
    public String activate(Model model, @PathVariable String code){
        boolean isActive = userService.activateUser(code);
        if (isActive){
            model.addAttribute("message", "Activation is success!");
        } else {
            model.addAttribute("message", "Account already active!");
        }
        return "activate";
    }

}
