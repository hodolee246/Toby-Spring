package com.example.toby.초난감DAO;

import com.example.toby.초난감DAO.daofactory.DaoFactory;
import com.example.toby.초난감DAO.user.User;
import com.example.toby.초난감DAO.UserDao;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.ContextConfiguration;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


import java.sql.SQLException;

@SpringBootTest
@ContextConfiguration(classes = { DaoFactory.class })
public class UserDaoTest {

    private UserDao dao;

    @Autowired
    private ApplicationContext context;

    @Autowired
    private UserDao userDao;

    @BeforeEach
    public void setUp() {
        System.out.println(this.context);
        System.out.println(this);
        this.dao = context.getBean("userDao", UserDao.class);
//        this.dao.setDataSource(this.dao.getDataSource());
//
//        userDao.setDataSource(userDao.getDataSource());
    }

    @Test
    @DisplayName("addAndGet 예제")
    public void addAndGet() throws SQLException, ClassNotFoundException {
        User user1 = new User("inwoo", "인우", "jiw");
        User user2 = new User("amuge", "아무개", "amg");

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));
        dao.add(user1);
        dao.add(user2);
        assertThat(dao.getCount(), is(2));

        User userGet1 = dao.get("inwoo");
        assertThat(userGet1.getName(), is(user1.getName()));
        assertThat(userGet1.getPwd(), is(user1.getPwd()));
        User userGet2 = dao.get("amuge");
        assertThat(userGet2.getName(), is(user2.getName()));
        assertThat(userGet2.getPwd(), is(user2.getPwd()));
    }
    @Test
    @DisplayName("deleteAllAndGetCount 예제")
    public void addAndGet2() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));

        User user = new User("inwoo", "전인우", "jiw");
        dao.add(user);
        assertThat(dao.getCount(), is(1));

        User user2 = dao.get("inwoo");
        assertThat(user.getName(), is(user2.getName()));
        assertThat(user.getPwd(), is(user2.getPwd()));
    }
    @Test
    @DisplayName("count 예제")
    public void count() throws SQLException, ClassNotFoundException {
        User user1 = new User("aaa111", "인우1", "abc1");
        User user2 = new User("aaa112", "인우2", "abc2");
        User user3 = new User("aaa113", "인우3", "abc3");
        User user4 = new User("aaa114", "인우4", "abc4");

        dao.deleteAll();
        assertThat(dao.getCount(), is(0));
        dao.add(user1);
        assertThat(dao.getCount(), is(1));
        dao.add(user2);
        assertThat(dao.getCount(), is(2));
        dao.add(user3);
        assertThat(dao.getCount(), is(3));
        dao.add(user4);
        assertThat(dao.getCount(), is(4));
    }
    @Test
    @DisplayName("get예외 예제")
    public void getUserFailed() throws SQLException, ClassNotFoundException {
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));
        // EmptyResultDataAccessException 이 발생 하게끔 Dao 를 구성
        // exception 객체를 활용할때
        EmptyResultDataAccessException thrown = Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            dao.get("??unknownId??");
        });
        // exception 객체를 활용하지 않을때
        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            dao.get("unknown_id");
        });
    }
}
