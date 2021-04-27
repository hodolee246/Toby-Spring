package com.example.toby.jiw.service.sql;

import com.example.toby.jiw.common.exception.SqlUpdateFailureException;
import com.example.toby.jiw.dao.sql.ConcurrentHashMapSqlRegistry;
import com.example.toby.jiw.dao.sql.SqlNotFoundException;
import com.example.toby.jiw.dao.sql.UpdatableSqlRegistry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractUpdatableSqlRegistryTest {
    UpdatableSqlRegistry sqlRegistry;

    @BeforeEach
    public void setUp() {
        sqlRegistry = createUpdatableSqlRegistry();
        sqlRegistry.registerSql("KEY1", "SQL1");
        sqlRegistry.registerSql("KEY2", "SQL2");
        sqlRegistry.registerSql("KEY3", "SQL3");
    }

    abstract protected UpdatableSqlRegistry createUpdatableSqlRegistry();

    @Test
    public void find() {
        checkFindResult("SQL1", "SQL2", "SQL3");
    }

    protected void checkFindResult(String expected1, String expected2, String expected3) {
        Assertions.assertEquals(sqlRegistry.findSql("KEY1"), expected1);
        Assertions.assertEquals(sqlRegistry.findSql("KEY2"), expected2);
        Assertions.assertEquals(sqlRegistry.findSql("KEY3"), expected3);
    }

    @Test
    public void unknownKey() {
        Assertions.assertThrows(SqlNotFoundException.class, () -> {
            sqlRegistry.findSql("SQL9999!@#$");
        });
    }

    @Test
    public void updateSingle() {
        sqlRegistry.updateSql("KEY2", "Modified2");
        checkFindResult("SQL1", "Modified2", "SQL3");
    }

    @Test
    public void updateMulti() {
        Map<String, String> sqlmap = new HashMap<>();
        sqlmap.put("KEY1", "Modified1");
        sqlmap.put("KEY3", "Modified3");

        sqlRegistry.updateSql(sqlmap);
        checkFindResult("Modified1", "SQL2", "Modified3");
    }

    @Test
    public void updateWithNotExistingKey() {
        Assertions.assertThrows(SqlUpdateFailureException.class, () -> {
            sqlRegistry.updateSql("SQL9999!@#$", "Modified2");
        });
    }
}
