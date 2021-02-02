package com.example.toby.초난감DAO.daofactory;

import com.example.toby.초난감DAO.UserDaoJdbc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {

    @Bean
    public UserDaoJdbc userDao() {
        return new UserDaoJdbc(dataSource());
    }

    @Bean
    public DataSource dataSource() {
        return new SingleConnectionDataSource("jdbc:h2:tcp://localhost/~/test", "sa", "", true);
    }
}
