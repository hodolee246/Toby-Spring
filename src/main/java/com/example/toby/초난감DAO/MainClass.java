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

        UserDao userDao1 = daoFactory.userDao();
        UserDao userDao2 = daoFactory.userDao();
        log.info("userDao: '{}'", userDao1);
        log.info("userDao: '{}'", userDao2);
        // ApplicationContext
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(DaoFactory.class);

        UserDao userDao3 = applicationContext.getBean("userDao", UserDao.class);
        UserDao userDao4 = applicationContext.getBean("userDao", UserDao.class);
        log.info("userDao: '{}'", userDao3);
        log.info("userDao: '{}'", userDao4);
    }
}
