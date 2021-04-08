package com.example.toby.jiw.config;

import com.example.toby.jiw.dao.UserDaoJdbc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;

@Configuration
public class DaoFactoryTest {

    @Bean
    public UserDaoJdbc userDao() {
        return new UserDaoJdbc(dataSource());
    }

    // Test용 DB라 가정
    @Bean
    public DataSource dataSource() {
        return new SingleConnectionDataSource("jdbc:h2:tcp://localhost/~/test", "sa", "", true);
    }
}