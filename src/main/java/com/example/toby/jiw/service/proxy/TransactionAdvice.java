package com.example.toby.jiw.service.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionAdvice implements MethodInterceptor {

    private final PlatformTransactionManager transactionManager; // 트랜잭션 기능용 트랜잭션 매니저

    public TransactionAdvice(PlatformTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            // 콜백을 호출시켜 타깃의 메소드를 실행한다. 타깃 메소드 호출 전후로 필요한 부가기능을 넣을 수 있다.
            Object ret = invocation.proceed();
            this.transactionManager.commit(status);
            return ret;
        } catch (RuntimeException e) {  // JDK 다이내믹 프록시가 제공하는 Meothd 와 달리 스프링의 MethodInvocation을 통해 예외를 포장하지 않고 타깃에게 그대로 보낸다.
            this.transactionManager.rollback(status);
            throw e;
        }
    }
}
