package com.example.toby.jiw.dao.sql;

import com.example.toby.jiw.common.exception.SqlRetrievalFailureException;

public interface SqlService {
    String getSql(String key) throws SqlRetrievalFailureException;
}
