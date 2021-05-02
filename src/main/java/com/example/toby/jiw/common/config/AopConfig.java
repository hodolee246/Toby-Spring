package com.example.toby.jiw.common.config;

import com.example.toby.jiw.service.proxy.TransactionAdvice;
import com.example.toby.jiw.service.UserService;
import com.example.toby.jiw.service.proxy.learningtest.Message;
import com.example.toby.jiw.service.proxy.learningtest.MessageFactoryBean;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class AopConfig {

    @Autowired
    PlatformTransactionManager transactionManager;

    @Bean
    public TransactionAdvice transactionAdvice() {
        return new TransactionAdvice(transactionManager);
    }

    @Bean
    public DefaultPointcutAdvisor transactionAdvisor() {
        return new DefaultPointcutAdvisor(transactionPointcut(), transactionAdvice());
    }

    @Bean
    public AspectJExpressionPointcut transactionPointcut() {
        AspectJExpressionPointcut expression = new AspectJExpressionPointcut();
        expression.setExpression("execution(* *..*ServiceImpl.upgrade*(..))");
        return expression;
    }

//    @Bean
//    public ProxyFactoryBean userServiceProxyFactoryBean() {
//        transactionAdvisor();
//        ProxyFactoryBean factoryBean = new ProxyFactoryBean();
//        factoryBean.setTarget(userService);
//        factoryBean.setInterceptorNames("transactionAdvisor");
//        return factoryBean;
//    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }

    // factory bean
    @Bean
    public MessageFactoryBean messageFactoryBean() {
        return new MessageFactoryBean("Factory Bean");
    }

    @Bean
    public Message message() throws Exception {
        return messageFactoryBean().getObject();
    }

}
