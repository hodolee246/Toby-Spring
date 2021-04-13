package com.example.toby.jiw.dao.sql;

public class SqlNotFoundException extends RuntimeException {

    String message;

    public SqlNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
