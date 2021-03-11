package com.example.toby.초난감DAO.config;

import com.example.toby.초난감DAO.proxy.MessageFactoryBean;
import com.example.toby.초난감DAO.proxy.TxProxyFactoryBean;
import com.example.toby.초난감DAO.service.UserService;
import com.example.toby.초난감DAO.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class FactoryBean {

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    PlatformTransactionManager transactionManager;

    @Bean
    public MessageFactoryBean message() {
        return new MessageFactoryBean("Factory Bean");
    }

    @Bean
    public TxProxyFactoryBean userService() {
        return new TxProxyFactoryBean(userServiceImpl, transactionManager, "upgradeLevels", UserService.class);
    }
}
