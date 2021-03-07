package com.example.toby.초난감DAO.config;

import com.example.toby.초난감DAO.service.DummyMailSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;

@Configuration
public class TobyConfig {

    @Bean
    public MailSender mailSender() {
        return new DummyMailSender();
    }

}
