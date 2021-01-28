package com.example.toby.초난감DAO;

import com.example.toby.초난감DAO.daofactory.DaoFactoryTest;
import com.example.toby.초난감DAO.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@DirtiesContext
@ContextConfiguration(classes = { DaoFactoryTest.class })
@SpringBootTest
public class UserDaoTest2 {

    @Autowired
    UserDaoJdbc userDao;

    @BeforeEach
    public void setUp() throws SQLException {
        DataSource dataSource = new SingleConnectionDataSource("jdbc:mysql://localhost:3306/sys?serverTimezone=UTC&characterEncoding=UTF-8", "root", "1234", true);
        userDao.setDataSource(dataSource);
    }

    @Test
    public void add() throws SQLException, ClassNotFoundException {
        userDao.deleteAll();
        User user = new User("asdf1234", "in", "abc123");
        userDao.add(user);
        User user2 = userDao.get("asdf1234");
        System.out.println(userDao);
        assertThat(user.getId(), is(user2.getId()));
    }

    @Test
    public void get() throws SQLException {
        User user = userDao.get("asdf1234");
        System.out.println(userDao);
        assertThat("asdf1234", is(user.getId()));
    }
}
