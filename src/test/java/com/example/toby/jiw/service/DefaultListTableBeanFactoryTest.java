package com.example.toby.jiw.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class DefaultListTableBeanFactoryTest {
    @Autowired
    DefaultListableBeanFactory bf;

    @Test
    public void beans() {
        for(String s : bf.getBeanDefinitionNames()) {
            System.out.println(s + "\t" + bf.getBean(s).getClass().getName());
        }
    }
}
