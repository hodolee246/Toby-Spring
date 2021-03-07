package com.example.toby.초난감DAO.daofactory;

import com.example.toby.초난감DAO.UserDaoJdbc;
import com.example.toby.초난감DAO.service.DummyMailSender;
import com.example.toby.초난감DAO.service.UserService;
import com.example.toby.초난감DAO.service.UserServiceImpl;
import com.example.toby.초난감DAO.service.UserServiceTx;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.mail.MailSender;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {

    @Bean
    public DataSource dataSource() {
        return new SingleConnectionDataSource("jdbc:h2:tcp://localhost/~/test", "sa", "", true);
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {  // dataSource 의 커넥션을 가져와 트랜잭션 처리해야 하기에 DataSource 를 받음
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public MailSender mailSender() {
        return new DummyMailSender();
    }

    @Bean
    public UserDaoJdbc userDao() {
        return new UserDaoJdbc(dataSource());
    }

    @Bean
    public UserServiceTx userService() {
        return new UserServiceTx(transactionManager(), userSErviceImpl());
    }

    @Bean
    public UserServiceImpl userSErviceImpl() {
        return new UserServiceImpl(userDao(), mailSender());
    }
}
