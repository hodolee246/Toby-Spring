package com.example.toby.jiw.service.sql;

import com.example.toby.jiw.dao.sql.ConcurrentHashMapSqlRegistry;
import com.example.toby.jiw.dao.sql.UpdatableSqlRegistry;

public class ConcurrentHashMapSqlRegistryTest extends AbstractUpdatableSqlRegistryTest {

    @Override
    protected UpdatableSqlRegistry createUpdatableSqlRegistry() {
        return new ConcurrentHashMapSqlRegistry();
    }
}
