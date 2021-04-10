package com.example.toby.jiw.dao.sql;

public interface SqlService {
    String getSql(String key) throws SqlRetrievalFailureException;
}
