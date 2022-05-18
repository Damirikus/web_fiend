package ru.gazizov.webfiend.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.gazizov.webfiend.service.UserServiceImpl;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) //??? добавили для того чтобы заработало @PreAuthorize("hasAuthority('ADMIN')")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;

    public WebSecurityConfig(UserServiceImpl userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                    .antMatchers("/", "/registration", "/static/**", "/images/**", "/activate/*")
                    .permitAll() //говорим, что только эта страница будет доступна всем
                    .anyRequest().authenticated()
                .and()
                    .formLogin()
                    .loginPage("/login") // и логин доступен всем
//                    .defaultSuccessUrl("/messages")
                    .permitAll()
                .and()
                    .logout()
                    .permitAll();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }
}