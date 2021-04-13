package com.example.toby.jiw.dao.템플릿콜백;

import java.io.IOException;

public interface LineCallBack<T> {
    T doSomethingWithLine(String line, T value) throws IOException;
}
