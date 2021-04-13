package com.example.toby.jiw.service.aop;

import com.example.toby.jiw.common.config.DaoFactory;
import com.example.toby.jiw.common.config.FactoryBean;
import com.example.toby.jiw.service.proxy.learningtest.Message;
import com.example.toby.jiw.service.proxy.learningtest.MessageFactoryBean;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

/** FactoryBean 학습 테스트
 *
 */
@SpringBootTest
@ContextConfiguration(classes = {FactoryBean.class, DaoFactory.class})
public class FactoryBeanTest {

    @Autowired
    ApplicationContext context;

    @Test
    public void getMessageFromFactoryBean() {
        Object message = context.getBean("message");
        Assertions.assertEquals(message.getClass(), Message.class);
        Assertions.assertEquals(((Message)message).getText(), "Factory Bean");
    }

    @Test
    public void getFactoryBean() throws Exception {
        Object factory = context.getBean("&message");
        Assertions.assertEquals(factory.getClass(), MessageFactoryBean.class);
    }
}
