package com.example.toby.jiw.common.config;

import com.example.toby.jiw.dao.UserDaoJdbc;
import com.example.toby.jiw.dao.sql.SqlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;

@Configuration
public class DaoFactoryTest {

    @Autowired SqlService sqlService;

    @Bean
    public UserDaoJdbc userDao() {
        return new UserDaoJdbc(sqlService, dataSource());
    }

    // Test용 DB라 가정
    @Bean
    public DataSource dataSource() {
        return new SingleConnectionDataSource("jdbc:h2:tcp://localhost/~/test", "sa", "", true);
    }
}
