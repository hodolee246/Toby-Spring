package com.example.toby.jiw.config;

import com.example.toby.jiw.dao.UserDaoJdbc;
import com.example.toby.jiw.service.DummyMailSender;
import com.example.toby.jiw.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.mail.MailSender;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DaoFactory {

    @Bean
    public DataSource dataSource() {
        return new SingleConnectionDataSource("jdbc:h2:tcp://localhost/~/test", "sa", "", true);    // ch6 aop @Transactional 을 사용한 DB테스트 시 H2는 실패하는 문제발생
//        return new SingleConnectionDataSource("jdbc:mysql://localhost:3306/sys?serverTimezone=UTC&characterEncoding=UTF-8", "root", "1234", true);
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
        Map<String ,String> sqlMap = new HashMap<>();
        sqlMap.put("add", "insert into users(id, name, pwd, level, login, recommend, email) values(?, ?, ?, ?, ?, ?, ?)");
        sqlMap.put("get", "select * from users where id = ?");
        sqlMap.put("getAll", "select * from users order by id");
        sqlMap.put("deleteAll", "delete from users");
        sqlMap.put("getCount", "select count(*) from users");
        sqlMap.put("update", "update users set name = ?, pwd = ?, level = ?, login = ?, recommend = ?, email = ? where id = ?");
        return new UserDaoJdbc(sqlMap, dataSource());
    }

    @Bean
    public UserServiceImpl userService() {
        return new UserServiceImpl(userDao(), mailSender());
    }

}
