package com.example.toby.초난감DAO.aop;

import com.example.toby.초난감DAO.proxy.Hello;
import com.example.toby.초난감DAO.proxy.HelloTarget;
import com.example.toby.초난감DAO.proxy.HelloUppercase;
import com.example.toby.초난감DAO.proxy.UppercaseHandler;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/** Hello 인터페이스를 통해 HelloTarget 오브젝트를 사용하는 클라이언트 역활 테스트
 *
 */
public class ProxyTest {

    @Test
    public void simpleProxy() {
        Hello hello = new HelloTarget();    // 타깃은 인터페이스로 생성
        Assertions.assertEquals(hello.sayHello("Toby"), "Hello Toby");
        Assertions.assertEquals(hello.sayHi("Toby"), "Hi Toby");
        Assertions.assertEquals(hello.sayThankYou("Toby"), "ThankYou Toby");
    }

    @Test
    public void upperProxy() {
        Hello proxiedHello = new HelloUppercase(new HelloTarget());    // 프록시를 통해 타깃 오브젝트에 접근하도록 구성
        Assertions.assertEquals(proxiedHello.sayHello("Toby"), "HELLO TOBY");
        Assertions.assertEquals(proxiedHello.sayHi("Toby"), "HI TOBY");
        Assertions.assertEquals(proxiedHello.sayThankYou("Toby"), "THANKYOU TOBY");
    }

    @Test
    public void dynamicProxy() {
        // params 1. 클래스 로더 2. 다이내믹 프록시가 구현할 인터페이스 3. 부가기능과 위임 관련 코드를 담고있는 InvocationHandler 구현 오브젝트
        Hello proxiedHello = (Hello) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {Hello.class}, new UppercaseHandler(new HelloTarget()));
    }

    @Test
    public void proxyFactoryBean() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());
        pfBean.addAdvice(new UppercaseAdvice());

        Hello proxiedHello = (Hello) pfBean.getObject();

        Assertions.assertEquals(proxiedHello.sayHello("Toby"), "HELLO TOBY");
        Assertions.assertEquals(proxiedHello.sayHi("Toby"), "HI TOBY");
        Assertions.assertEquals(proxiedHello.sayThankYou("Toby"), "THANKYOU TOBY");
    }

    @Test
    public void pointcutAdvisor() {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(new HelloTarget());

        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedName("sayH*");

        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));

        Hello proxiedHello = (Hello) pfBean.getObject();

        Assertions.assertEquals(proxiedHello.sayHello("Toby"), "HELLO TOBY");
        Assertions.assertEquals(proxiedHello.sayHi("Toby"), "HI TOBY");
        Assertions.assertEquals(proxiedHello.sayThankYou("Toby"), "ThankYou Toby");
    }

    class UppercaseAdvice implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            String ret = (String) invocation.proceed(); // 메소드 실행 시 타겟 오브젝트 전달필요 없음 (이미 타겟 오브젝트 정보를 알고있음)
            return ret.toUpperCase();   // 부가기능
        }
    }
}
