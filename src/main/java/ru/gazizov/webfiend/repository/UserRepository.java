package ru.gazizov.webfiend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gazizov.webfiend.model.User;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    //метод для посика юзера по имени
    User findByUsername(String username);

    User findByActivationCode(String code);
}
