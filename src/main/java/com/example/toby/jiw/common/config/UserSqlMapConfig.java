package com.example.toby.jiw.common.config;

import com.example.toby.jiw.dao.UserDao;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class UserSqlMapConfig implements SqlMapConfig {
    @Override
    public Resource getSqlMapResource() {
        return new ClassPathResource("/sql/sqlmap.xml", UserDao.class);
    }
}
