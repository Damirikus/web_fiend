package ru.gazizov.webfiend.service;

import org.hibernate.Hibernate;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
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
        User user = userRepository.findByUsername(username);
        if (user == null){
            throw new UsernameNotFoundException("User not found!");
        }
        return user;
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
        //получаем в сет стрингов все роли
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

        if ((existsEmail != null && !existsEmail.equals(email))
                || (email != null && !email.equals(existsEmail))){
            user.setEmail(email);
            if (!email.isEmpty()){
                user.setActivationCode(UUID.randomUUID().toString());
                sendMessage(user);
            }
        }

        user.setPassword(passwordEncoder.encode(password));

        userRepository.save(user);
    }

    @Override
    public void subscribe(User user, User currentUser) {

        user.getSubscribers().add(currentUser);
        userRepository.save(user);
    }

    @Override
    public void unSubscribe(User user, User currentUser) {
        user.getSubscribers().remove(currentUser);
        userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id).get();
    }

}
