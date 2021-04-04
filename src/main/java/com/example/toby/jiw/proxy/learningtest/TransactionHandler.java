package com.example.toby.jiw.proxy.learningtest;

import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TransactionHandler implements InvocationHandler {

    private final Object target;  // 타깃
    private final PlatformTransactionManager transactionManager; // 트랜잭션 기능용 트랜잭션 매니저
    private final String patter;  // 트랜잭션을 적용할 메소드 이름 패턴

    public TransactionHandler(Object target, PlatformTransactionManager transactionManager, String patter) {
        this.target = target;
        this.transactionManager = transactionManager;
        this.patter = patter;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if(method.getName().startsWith(patter)) {
            return invokeInTransaction(method, args);
        } else {
            return method.invoke(target, args);
        }
    }

    private Object invokeInTransaction(Method method, Object[] args) throws Throwable {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            Object ret = method.invoke(target, args);
            this.transactionManager.commit(status);
            return ret;
        } catch (InvocationTargetException e) {
            this.transactionManager.rollback(status);
            throw e.getTargetException();
        }
    }
}