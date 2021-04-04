package com.example.toby.jiw.config;

import com.example.toby.jiw.proxy.learningtest.MessageFactoryBean;
import com.example.toby.jiw.proxy.TransactionAdvice;
import com.example.toby.jiw.proxy.learningtest.pointcut.NameMatchClassMethodPointcut;
import com.example.toby.jiw.service.UserServiceImpl;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class FactoryBean {

    @Bean
    public MessageFactoryBean message() {
        return new MessageFactoryBean("Factory Bean");
    }

}
