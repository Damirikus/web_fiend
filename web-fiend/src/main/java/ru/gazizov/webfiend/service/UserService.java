package ru.gazizov.webfiend.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.gazizov.webfiend.model.User;

import java.util.List;
import java.util.Map;


/*
UserService. Содержит методы для бизнес-логики приложения.
Этот класс реализует интерфейс UserDetailsService (необходим для Spring Security),
в котором нужно переопределить один метод loadUserByUsername().
 */

public interface UserService extends UserDetailsService {
    boolean addUser(User user);
    boolean activateUser(String code);
    List<User> findAll();
    void delete(User user);
    void saveUser(String username, Map<String, String> form, User user);

    void updateProfile(User user, String email, String password);
}
