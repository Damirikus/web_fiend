package ru.gazizov.webfiend.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;


/*Для того, чтобы в дальнейшим использовать класс User в Spring Security,
 он должен реализовывать интерфейс UserDetails. Для этого нужно переопределить все его методы.
 */
@Entity
@Table(name = "usr") //это на всякий случай, чтобы таблица не называлась User, чтобы избежать вероятных ошибок
public class User implements UserDetails, Serializable {

    @Serial
    private static final long serialVersionUID = -1490940687339759591L;

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

    private String activationCode;
    private boolean active;

    //обратная связь с ManyToOne в Message, указали author, так как в Message так называется поле
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Message> messages;

    @ManyToMany // укаываем, что в этой таблице будут храниться как подписчики так и подписки
    @JoinTable(name = "usr_subscriptions",
            joinColumns = { @JoinColumn(name = "channel_id") },
            inverseJoinColumns = { @JoinColumn(name = "subscriber_id")} )
    private Set<User> subscribers = new HashSet<>();

    @ManyToMany //здесь у нас таблица с нашими подписками
    @JoinTable(name = "usr_subscriptions",
            joinColumns = { @JoinColumn(name = "subscriber_id") },
            inverseJoinColumns = { @JoinColumn(name = "channel_id")} )
    private Set<User> subscriptions = new HashSet<>();


    //fetch позволяет сразу подгружать всю таблицу ролей (жадный способ)
    @ElementCollection(targetClass = Role.class, fetch = FetchType.EAGER)
    //здесь создаем таблицу с ролями и говорим, что они будут соединены с юзером по id
    @CollectionTable(name = "usr_role", joinColumns = @JoinColumn(name = "usr_id"))
    //здесь говорим, что роли будут храниться в стринг
    @Enumerated(EnumType.STRING)
    private Set<Role> roles;

    public User() {
    }

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

    public Set<Message> getMessages() {
        return messages;
    }

    public void setMessages(Set<Message> messages) {
        this.messages = messages;
    }

    public Set<User> getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(Set<User> subscribers) {
        this.subscribers = subscribers;
    }

    public Set<User> getSubscriptions() {
        return subscriptions;
    }

    public void setSubscriptions(Set<User> subscriptions) {
        this.subscriptions = subscriptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return getId().equals(user.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\n' +
                ", email='" + email + '\n' +
                ", password='" + password + '\n' +
                ", activationCode='" + activationCode + '\n' +
                ", active=" + active +'\n' +
                ", messages=" + messages +'\n' +
                ", subscribers=" + subscribers +'\n' +
                ", subscriptions=" + subscriptions +'\n' +
                ", roles=" + roles +
                '}';
    }
}
