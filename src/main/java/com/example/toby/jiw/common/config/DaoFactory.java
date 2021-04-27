package com.example.toby.jiw.common.config;

import com.example.toby.jiw.dao.UserDaoJdbc;
import com.example.toby.jiw.dao.sql.*;
import com.example.toby.jiw.service.DummyMailSender;
import com.example.toby.jiw.service.UserServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mail.MailSender;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import javax.sql.DataSource;

@Configuration
public class DaoFactory {

    @Bean
    public UserDaoJdbc userDao() {
        return new UserDaoJdbc(sqlService(), dataSource());
    }

    @Bean
    public DataSource dataSource() {
        return new SingleConnectionDataSource("jdbc:h2:tcp://localhost/~/test", "sa", "", true);    // ch6 aop @Transactional 을 사용한 DB테스트 시 H2는 실패하는 문제발생
//        return new SingleConnectionDataSource("jdbc:mysql://localhost:3306/sys?serverTimezone=UTC&characterEncoding=UTF-8", "root", "1234", true);
    }

    @Bean
    public UserServiceImpl userService() {
        return new UserServiceImpl(userDao(), mailSender());
    }

    @Bean
    public MailSender mailSender() {
        return new DummyMailSender();
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {  // dataSource 의 커넥션을 가져와 트랜잭션 처리해야 하기에 DataSource 를 받음
        return new DataSourceTransactionManager(dataSource());
    }
    // ch7
    @Bean
    public OxmSqlService sqlService() {
        OxmSqlService sqlService = new OxmSqlService();
        sqlService.setUnmarshaller(unmarshaller());
        sqlService.setSqlRegistry(sqlRegistry());
        return sqlService;
    }

    @Bean
    public JaxbXmlSqlReader sqlReader() {
        JaxbXmlSqlReader reader = new JaxbXmlSqlReader();
        reader.setSqlmapFile("/sql/sqlmap.xml");
        return reader;
    }

    @Bean
    public EmbeddedDbSqlRegistry sqlRegistry() {
        EmbeddedDbSqlRegistry sqlRegistry = new EmbeddedDbSqlRegistry();
        sqlRegistry.setDataSource(embeddedDatabase());
        return sqlRegistry;
    }

    @Bean
    public Jaxb2Marshaller unmarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("com.example.toby.jiw.dao.sql.jaxb");
        return marshaller;
    }

    @Bean
    public DataSource embeddedDatabase() {
        return new EmbeddedDatabaseBuilder()
                .setName("embeddedDatabase")
                .setType(EmbeddedDatabaseType.HSQL)
                .addScript("/sql/embedded-db-schema.sql")
                .build();
    }

}
