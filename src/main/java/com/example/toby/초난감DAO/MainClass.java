package com.example.toby.초난감DAO;

import com.example.toby.초난감DAO.daofactory.DaoFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

@Slf4j
public class MainClass {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        // DaoFactory
        DaoFactory daoFactory = new DaoFactory();

        UserDaoJdbc userDao1 = daoFactory.userDao();
        UserDaoJdbc userDao2 = daoFactory.userDao();
        log.info("userDao: '{}'", userDao1);
        log.info("userDao: '{}'", userDao2);
        // ApplicationContext
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(DaoFactory.class);

        UserDaoJdbc userDao3 = applicationContext.getBean("userDao", UserDaoJdbc.class);
        UserDaoJdbc userDao4 = applicationContext.getBean("userDao", UserDaoJdbc.class);
        log.info("userDao: '{}'", userDao3);
        log.info("userDao: '{}'", userDao4);
    }
}
