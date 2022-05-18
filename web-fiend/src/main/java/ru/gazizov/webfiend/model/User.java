package ru.gazizov.webfiend.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;


/*Для того, чтобы в дальнейшим использовать класс Userв Spring Security,
 он должен реализовывать интерфейс UserDetails. Для этого нужно переопределить все его методы.
 */
@Entity
@Table(name = "usr") //это на всякий слцчай, чтобы таблица не называлась User, чтобы избежать вероятных ошибок
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Username cannot be empty!")
    private String username;

    @Email(message = "Email is not correct!")
    @NotBlank(message = "Email cannot be empty!")
    private String email;

    @NotBlank(message = "Password cannot be empty!")
    private String password;

    @Transient
    private String password2;

    private String activationCode;
    private boolean active;

    //fetch позволяет сразу подгружать всю таблицу ролей (жадный способ)
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    //здесь создаем таблицу с ролями и говорим, что они будут соединены с юзером по id
    @CollectionTable(name = "usr_role", joinColumns = @JoinColumn(name = "usr_id"))
    //здесь говорим, что роли будут храниться в стринг
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public boolean isAdmin(){
        return roles.contains(Role.ADMIN);
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    //доступность аккаунта так же определяется полем active
    @Override
    public boolean isEnabled() {
        return isActive();
    }

    //возвращаем все роли для security
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }
}
