package ru.gazizov.webfiend.model;


import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

/*
Role. Этот класс должен реализовывать интерфейс GrantedAuthority,
в котором необходимо переопределить только один метод getAuthority() (возвращает имя роли).
Имя роли должно соответствовать шаблону: «ROLE_ИМЯ», например, ROLE_USER.
Кроме конструктора по умолчанию необходимо добавить еще пару публичных конструкторов:
первый принимает только id, второй id и name.
 */

public enum Role implements GrantedAuthority, Serializable {
    USER, ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }
}
