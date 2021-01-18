package com.example.toby.초난감DAO.템플릿콜백;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
    public Integer calcSum(String filepath) throws IOException {
        LineCallBack<Integer> sumCallback = new LineCallBack<Integer>() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) throws IOException {
                return value + Integer.valueOf(line);
            }
        };
        return lineReadTemplate(filepath, sumCallback, 0);
    }

    public Integer calcMultiply(String filepath) throws IOException {
        LineCallBack<Integer> multiplyCallback = new LineCallBack<Integer>() {
            @Override
            public Integer doSomethingWithLine(String line, Integer value) throws IOException {
                return value * Integer.valueOf(line);
            }
        };
        return lineReadTemplate(filepath, multiplyCallback, 1);
    }

    public String addString(String filepath) throws IOException {
        LineCallBack<String> stringAddCallback = new LineCallBack<String>() {
            @Override
            public String doSomethingWithLine(String line, String value) throws IOException {
                return value + line;
            }
        };
        return lineReadTemplate(filepath, stringAddCallback, "");
    }

    public <T> T lineReadTemplate(String filepath, LineCallBack<T> callBack, T initVal) throws IOException {
        try(BufferedReader br = new BufferedReader(new FileReader(filepath))) {
            T res = initVal;
            String line = null;
            while((line = br.readLine()) != null) {
                res = callBack.doSomethingWithLine(line, res);
            }
            return res;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }

    public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) throws IOException {
        try(BufferedReader br = new BufferedReader((new FileReader(filepath)))) {
            int ret = callback.doSomethingWithReader(br);
            return ret;
        } catch (IOException e) {
            System.out.println(e.getMessage());
            throw e;
        }
    }
}
