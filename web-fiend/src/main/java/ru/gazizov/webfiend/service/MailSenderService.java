package ru.gazizov.webfiend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailSenderService {
    @Value("${spring.mail.username}")
    private String username;

    //spring cам создаст бин для этой зависимости, а так же возмет конфиги для бина из проперти
    private final JavaMailSender javaMailSender;

    public MailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void send(String emailTo, String title, String text){
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(username); //отпрпвитель
        mailMessage.setSubject(title); //название письма
        mailMessage.setTo(emailTo); //кому
        mailMessage.setText(text); //тело сообщения

        javaMailSender.send(mailMessage);
    }
}
