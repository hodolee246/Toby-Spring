package com.example.toby.jiw.service.proxy.learningtest;

public class Message {
    String text;

    private Message(String text) {  // 접근제어자가 private 이기에 외부에서 생성 불가능
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public static Message newMessage(String text) { // 생성을 하려면 static 메소드를 통해 새로운 객체를 생성해야함
        return new Message(text);
    }
}
