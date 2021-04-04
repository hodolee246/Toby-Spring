package com.example.toby.jiw.proxy.learningtest;

import org.springframework.beans.factory.FactoryBean;

public class MessageFactoryBean implements FactoryBean<Message> {
    String text;

    public MessageFactoryBean(String text) {
        this.text = text;
    }

    @Override
    public Message getObject() throws Exception {   // 실제 빈으로 사용될 오브젝트를 직접 생성한다.
        return Message.newMessage(this.text);
    }

    @Override
    public Class<?> getObjectType() {
        return Message.class;
    }

    @Override
    public boolean isSingleton() {  // 요청시 새로운 객체를 생성하기에 싱글톤이 아니다.
        return false;
    }
}
