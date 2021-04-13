package com.example.toby.jiw.common.config;

import com.example.toby.jiw.dao.UserDao;
import com.example.toby.jiw.service.proxy.TransactionAdvice;
import com.example.toby.jiw.service.UserService;
import com.example.toby.jiw.service.UserServiceImpl;
import com.example.toby.jiw.domain.user.User;
import com.example.toby.jiw.service.proxy.learningtest.MessageFactoryBean;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.MailSender;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class AopConfig {

    @Autowired
    UserService userService;

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

    @Bean
    public ProxyFactoryBean userServiceProxyFactoryBean() {
        transactionAdvisor();
        ProxyFactoryBean factoryBean = new ProxyFactoryBean();
        factoryBean.setTarget(userService);
        factoryBean.setInterceptorNames("transactionAdvisor");
        return factoryBean;
    }

    @Bean
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        return new DefaultAdvisorAutoProxyCreator();
    }

    // factory bean
    @Bean
    public MessageFactoryBean message() {
        return new MessageFactoryBean("Factory Bean");
    }

    @Autowired UserDao userDao;
    @Autowired MailSender mailSender;

    @Bean
    public TestUserServiceImpl testUserService() {
        return new TestUserServiceImpl(userDao, mailSender);
    }

    /**
     * 자동 프록시 생성기 테스트용 TestUserServiceImpl
     */
    static class TestUserServiceImpl extends UserServiceImpl {

        private String id = "madnite1";

        public TestUserServiceImpl(UserDao userDao, MailSender mailSender) {
            super(userDao, mailSender);
        }

        @Override
        protected void upgradeLevel(User user) {
            if(user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    /**
     * 테스트용 Exception
     */
    static class TestUserServiceException extends RuntimeException {
    }

}
