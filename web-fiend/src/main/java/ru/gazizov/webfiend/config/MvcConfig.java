package ru.gazizov.webfiend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//этот класс был необходим при подключении spring security
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${upload.path}")
    private String path;


    /*
    Страница login обрабатывается Spring Security контроллером по умолчанию,
    поэтому для неё отдельный контроллер не требуется.
     */
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/login").setViewName("login");
    }

    @Override // для того, чтобы раздавать загруженный файл
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**") // указываем паттерн откула брать картинки
                .addResourceLocations("file://" + path + "/"); // и размещение каждой из этих картинок
        registry.addResourceHandler("/static/**") // раздаем весь файл статик и все стили
                .addResourceLocations("classpath:/static/");  // classpath говорит, что искать в файлах проекта
    }

}
