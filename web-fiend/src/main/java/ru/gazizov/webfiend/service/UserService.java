package ru.gazizov.webfiend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.gazizov.webfiend.model.Role;
import ru.gazizov.webfiend.model.User;
import ru.gazizov.webfiend.repository.UserRepository;

import java.util.Collections;
import java.util.UUID;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final MailSenderService mailSender;

    public UserService(UserRepository userRepository, MailSenderService mailSender) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    public String addUser(User user){
        User userByDb = userRepository.findByUsername(user.getUsername());
        if (userByDb != null){
            return "User already exists!";
        }
        if (user.getPassword().isEmpty()) {
            return "Please write the password!";
        }
        user.setActive(false);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        userRepository.save(user);

        if (!user.getEmail().isEmpty()){

            String message = String.format("Hello, %s\n" +
                "Follow the link to confirm your email: http://localhost:8080/activate/%s",
                    user.getUsername(),
                    user.getActivationCode());
            mailSender.send(user.getEmail(), "Activation code", message);
        }
        return "yes";
    }

    public boolean activateUser(String code) {
        User user = userRepository.findByActivationCode(code);
        if (user == null)
             return false;
        user.setActive(true);
        user.setActivationCode("Approved");
        userRepository.save(user);
        return true;
    }
}
