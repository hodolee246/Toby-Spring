package com.example.toby.jiw.service.proxy.learningtest;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import java.lang.reflect.Proxy;

public class TxProxyFactoryBean implements FactoryBean<Object> {

    Object target;
    PlatformTransactionManager taTransactionManager;
    String patten;
    Class<?> serviceInterface;

    public TxProxyFactoryBean(Object target, PlatformTransactionManager taTransactionManager, String patten, Class<?> serviceInterface) {
        this.target = target;
        this.taTransactionManager = taTransactionManager;
        this.patten = patten;
        this.serviceInterface = serviceInterface;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public void setTaTransactionManager(PlatformTransactionManager taTransactionManager) {
        this.taTransactionManager = taTransactionManager;
    }

    public void setPatten(String patten) {
        this.patten = patten;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        this.serviceInterface = serviceInterface;
    }

    @Override
    public Object getObject() throws Exception {
        TransactionHandler txHandler = new TransactionHandler(target, taTransactionManager, patten);
        return Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] {serviceInterface}, txHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return serviceInterface;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
