package com.example.toby.jiw.service.aop;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/** 리플렉션을 이용한 다이나믹 프록시 학습 테스트
 *
 */
public class ReflectionTest {

    @Test
    public void invokeMethod() throws Exception {
        String name = "JeonInWoo";

        // length
        Assertions.assertEquals(name.length(), 9);

        Method lengthMethod = String.class.getMethod("length");
        Assertions.assertEquals((Integer)lengthMethod.invoke(name), 9);

        // chartAt
        Assertions.assertEquals(name.charAt(0), 'J');

        Method chartAtMethod = String.class.getMethod("charAt", int.class);
        Assertions.assertEquals(chartAtMethod.invoke(name, 0), 'J');
    }
}
