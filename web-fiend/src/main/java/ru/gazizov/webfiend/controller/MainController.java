package ru.gazizov.webfiend.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.gazizov.webfiend.model.Message;
import ru.gazizov.webfiend.model.User;
import ru.gazizov.webfiend.repository.MessageRepository;
import ru.gazizov.webfiend.service.UserService;
import ru.gazizov.webfiend.service.UserServiceImpl;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Controller
public class MainController {

    @Value("${upload.path}") //из проперти берем пав
    private String path;

    //через конструктор автоматически идет внедрение зависимости
    private final MessageRepository messageRepository;
    private final UserService userService;

    public MainController(MessageRepository messageRepository, UserServiceImpl userService) {
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
    public String addMessage(@AuthenticationPrincipal User user,
                             @Valid Message message, //првоеряю валидацию полей
                             BindingResult bindingResult, //здесь отобразятся все ошибки валидации
                             Model model, // BindingResult должен идти до Model, иначе ошибки сразу могут отобразиться во вью?
                             @RequestParam("file") MultipartFile file) throws IOException {

            message.setAuthor(user);
            message.setDate(LocalDateTime.now());

            if (bindingResult.hasErrors()){
                Map<String, String> errors = ControllersUtils.getErrors(bindingResult);
                model.mergeAttributes(errors);
//                model.addAttribute("message", message);
            } else {
                if (file != null && !file.getOriginalFilename().isEmpty()){

                    String uuidFile = UUID.randomUUID().toString(); // создаем рандомное имя файлу
                    String result = uuidFile + "." + file.getOriginalFilename(); //собираем полное имя
                    message.setFilename(result); // устанавливаем имя файла в поле мессаджа
                    file.transferTo(new File(path + "/" + result)); //загружаем файл
                }
                messageRepository.save(message);
            }
        Iterable<Message> messages = messageRepository.findAll();
        model.addAttribute("messages", messages);
        return "internal/messages";
    }

    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("user", user);
        return "internal/profile";
    }

    @GetMapping("/profile/edit")
    public String profileEdit(Model model, @AuthenticationPrincipal User user){
        model.addAttribute("user", user);
        return "internal/profile-edit";
    }

    @PostMapping("/profile/edit")
    public String saveProfileChanges(@AuthenticationPrincipal User user,
                                     @RequestParam String email,
                                     @RequestParam String password){
        userService.updateProfile(user, email, password);
        return "redirect:/profile";
    }

    @GetMapping("/registration")
    public String register(){
        return "registration";
    }

    @PostMapping("/registration")
    public String registerPost(@Valid User user,
                               BindingResult bindingResult,
                               Model model){
        if (user.getPassword() != null && !user.getPassword().equals(user.getPassword2())){
            model.addAttribute("passwordError", "Passwords are not equals!");
        }

        if (bindingResult.hasErrors()){
            Map<String, String> errors = ControllersUtils.getErrors(bindingResult);
            model.mergeAttributes(errors);
            return "registration";
        }

        if (!userService.addUser(user)){
            model.addAttribute("message", "User already exists!");
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
