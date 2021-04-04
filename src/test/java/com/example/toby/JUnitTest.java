package com.example.toby;

import com.example.toby.jiw.config.DaoFactory;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@ContextConfiguration(classes = { DaoFactory.class })
@SpringBootTest
public class JUnitTest {

    @Autowired
    ApplicationContext applicationContext;

    static ApplicationContext contextObject = null;
    static Set<JUnitTest> testObject = new HashSet<>();

    @Test
    public void test1() {
        assertThat(testObject, not(Matchers.hasItem(this)));
        testObject.add(this);
        assertThat(contextObject == null || contextObject == this.applicationContext, is(true));
        contextObject = this.applicationContext;
    }

    @Test
    public void test2() {
        assertThat(testObject, not(Matchers.hasItem(this)));
        testObject.add(this);
        Assertions.assertTrue(contextObject == null || contextObject == this.applicationContext);
        contextObject = this.applicationContext;
    }
    @Test
    public void test3() {
        assertThat(testObject, not(Matchers.hasItem(this)));
        testObject.add(this);
        assertThat(contextObject, Matchers.either(is(nullValue())).or(is(this.applicationContext)));
        contextObject = this.applicationContext;
    }
}
