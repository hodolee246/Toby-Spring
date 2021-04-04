package com.example.toby.jiw.aop;

import com.example.toby.jiw.proxy.learningtest.Hello;
import com.example.toby.jiw.proxy.learningtest.HelloTarget;
import com.example.toby.jiw.proxy.learningtest.HelloUppercase;
import com.example.toby.jiw.proxy.learningtest.UppercaseHandler;
import com.example.toby.jiw.proxy.learningtest.pointcut.Bean;
import com.example.toby.jiw.proxy.learningtest.pointcut.Target;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.Pointcut;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;

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

    @Test
    public void classNamePointcutAdvisor() {
        // pointcut
        NameMatchMethodPointcut classMethodPointcut = new NameMatchMethodPointcut() {
            @Override
            public ClassFilter getClassFilter() {
                return aClass -> aClass.getSimpleName().startsWith("HelloT");
            }
        };
        classMethodPointcut.setMappedName("sayH*");

        // test
        checkAdvice(new HelloTarget(), classMethodPointcut, true);

        class HelloWorld extends HelloTarget {};
        checkAdvice(new HelloWorld(), classMethodPointcut, false);

        class HelloToby extends HelloTarget {};
        checkAdvice(new HelloToby(), classMethodPointcut, true);
    }

    private void checkAdvice(Object target, Pointcut pointcut, boolean advice) {
        ProxyFactoryBean pfBean = new ProxyFactoryBean();
        pfBean.setTarget(target);
        pfBean.addAdvisor(new DefaultPointcutAdvisor(pointcut, new UppercaseAdvice()));
        Hello proxiedHello = (Hello) pfBean.getObject();

        if(advice) {
            Assertions.assertEquals(proxiedHello.sayHello("Toby"), "HELLO TOBY");
            Assertions.assertEquals(proxiedHello.sayHi("Toby"), "HI TOBY");
            Assertions.assertEquals(proxiedHello.sayThankYou("Toby"), "ThankYou Toby");
        } else {
            Assertions.assertEquals(proxiedHello.sayHello("Toby"), "Hello TOBY");
            Assertions.assertEquals(proxiedHello.sayHi("Toby"), "Hi TOBY");
            Assertions.assertEquals(proxiedHello.sayThankYou("Toby"), "ThankYou Toby");
        }
    }

    @Test
    public void methodSignaturePointcut() throws SecurityException, NoSuchMethodException{
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(public int com.example.toby.jiw.proxy.learningtest.pointcut.Target.minus(int,int) throws java.lang.RuntimeException)");

        // target.minus()
        Assertions.assertEquals(pointcut.getClassFilter().matches(Target.class) && pointcut.getMethodMatcher().matches(Target.class.getMethod("minus", int.class, int.class), null), true);

        // Target.plus()
        Assertions.assertEquals(pointcut.getClassFilter().matches(Target.class) && pointcut.getMethodMatcher().matches(Target.class.getMethod("plus", int.class, int.class), null), false);

        // Bean Method()
        Assertions.assertEquals(pointcut.getClassFilter().matches(Bean.class) && pointcut.getMethodMatcher().matches(Target.class.getMethod("method"), null), false);

    }

    class UppercaseAdvice implements MethodInterceptor {
        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            String ret = (String) invocation.proceed(); // 메소드 실행 시 타겟 오브젝트 전달필요 없음 (이미 타겟 오브젝트 정보를 알고있음)
            return ret.toUpperCase();   // 부가기능
        }
    }
}
