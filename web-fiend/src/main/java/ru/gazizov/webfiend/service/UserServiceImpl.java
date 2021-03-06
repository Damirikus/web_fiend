package ru.gazizov.webfiend.service;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.gazizov.webfiend.model.Role;
import ru.gazizov.webfiend.model.User;
import ru.gazizov.webfiend.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl  implements UserService{
    private final UserRepository userRepository;
    private final MailSenderService mailSender;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, MailSenderService mailSender, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean addUser(User user){
        User userByDb = userRepository.findByUsername(user.getUsername());
        if (userByDb != null){
            return false;
        }
        user.setActive(true);
        user.setRoles(Collections.singleton(Role.USER));
        user.setActivationCode(UUID.randomUUID().toString());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        sendMessage(user);
        return true;
    }

    private void sendMessage(User user){
        if (!user.getEmail().isEmpty()){

            String message = String.format("Hello, %s\n" +
                            "Follow the link to confirm your email: http://localhost:8080/activate/%s",
                    user.getUsername(),
                    user.getActivationCode());
            mailSender.send(user.getEmail(), "Activation code", message);
        }
    }

    @Override
    public boolean activateUser(String code) {
        User user = userRepository.findByActivationCode(code);
        if (user == null)
             return false;
//        user.setActive(true);
        user.setActivationCode("Approved");
        userRepository.save(user);
        return true;
    }

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public void delete(User user) {
        userRepository.delete(user);
    }

    @Override
    public void saveUser(String username, Map<String, String> form, User user) {
        user.setUsername(username);
        //???????????????? ?? ?????? ???????????????? ?????? ????????
        Set<String> roles = Arrays.stream(Role.values()).map(Role::name).collect(Collectors.toSet());
        user.getRoles().clear();

        for (String key : form.keySet()){
            if (roles.contains(key)){
                user.getRoles().add(Role.valueOf(key));
            }
        }
        userRepository.save(user);
    }

    @Override
    public void updateProfile(User user, String email, String password) {
        String existsEmail = user.getEmail();

        if ((existsEmail != null && !existsEmail.equals(email)) || (email != null && !email.equals(existsEmail))){
            user.setEmail(email);
            if (!email.isEmpty()){
                user.setActivationCode(UUID.randomUUID().toString());
                sendMessage(user);
            }
        }
        if (!password.isEmpty()){
            user.setPassword(password);
        }
        userRepository.save(user);
    }
}
