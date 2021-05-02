package com.example.toby.jiw.common.config;

import com.example.toby.jiw.common.config.annotaion.EnableSqlService;
import com.example.toby.jiw.dao.UserDao;
import com.example.toby.jiw.dao.UserDaoJdbc;
import com.example.toby.jiw.dao.sql.*;
import com.example.toby.jiw.service.DummyMailSender;
import com.example.toby.jiw.service.TestUserServiceImpl;
import com.example.toby.jiw.service.UserServiceImpl;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.mail.MailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.Driver;

@Configuration
@ComponentScan(basePackages = "com.example.toby.jiw.dao")
@EnableTransactionManagement
@EnableSqlService
@Import({AppContext.ProductionAppContext.class, AppContext.TestAppContext.class, SqlServiceContext.class})
//@PropertySource("/application.properties") 이거랑 PropertySourcePlac...config 는 없어도 스프링부트가 자동으로 해준다고함
public class AppContext implements SqlMapConfig {

    @Value("${spring.datasource.driver-class-name}")
    private Class<? extends Driver> driverClass;

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public DataSource dataSource() {
//        SimpleDriverDataSource ds = new SimpleDriverDataSource(); value로 받아온 driver class가 문제있나 자꾸 타입 미스매치 뜨는거 같은데 일단 넘어가자
//        ds.setDriverClass(driverClass);
//        ds.setUrl(url);
//        ds.setUsername(userName);
//        ds.setPassword(password);
//
//        return ds;
        return new SingleConnectionDataSource("jdbc:h2:tcp://localhost/~/test", "sa", "", true);    // ch6 aop @Transactional 을 사용한 DB테스트 시 H2는 실패하는 문제발생
//        return new SingleConnectionDataSource("jdbc:mysql://localhost:3306/sys?serverTimezone=UTC&characterEncoding=UTF-8", "root", "1234", true);
    }

    @Bean
    public DataSourceTransactionManager transactionManager() {  // dataSource 의 커넥션을 가져와 트랜잭션 처리해야 하기에 DataSource 를 받음
        return new DataSourceTransactionManager(dataSource());
    }

    @Override
    public Resource getSqlMapResource() {
        return new ClassPathResource("/sql/sqlmap.xml", UserDao.class);
    }

    @Configuration
    @Profile("production")
    public static class ProductionAppContext {
        @Autowired
        UserDao userDao;

        @Bean
        public MailSender mailSender() {    //  운영용 메일
            JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
            mailSender.setHost("localhost");
            return mailSender;
        }

        @Bean
        public UserServiceImpl userService() {
            UserServiceImpl userService = new UserServiceImpl();
            userService.setUserDao(userDao);
            return new UserServiceImpl(userDao, mailSender());
        }
    }

    @Configuration
    @Profile("test")
    public static class TestAppContext {
        @Autowired
        UserDao userDao;

        @Bean
        public TestUserServiceImpl testUserService() {
            return new TestUserServiceImpl(userDao, mailSender());
        }

        @Bean
        public MailSender mailSender() {
            return new DummyMailSender();
        }
    }
}
