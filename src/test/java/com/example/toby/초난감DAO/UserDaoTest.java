package com.example.toby.초난감DAO;

import com.example.toby.초난감DAO.Exception.DuplicateUserIdException;
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
import java.util.List;

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
    @Test
    @DisplayName("getAll 예제")
    public void getAll() {
        User user1 = new User("abc1", "inu", "abc1234");
        User user2 = new User("abc2", "inWoo", "abc1");
        User user3 = new User("abc3", "jiw", "abc12");

        dao.deleteAll();

        List<User> users0 = dao.getAll();
        assertThat(users0.size(), is(0));

        dao.add(user1);
        List<User> users1 = dao.getAll();
        assertThat(users1.size(), is(1));
        checkSameUser(user1, users1.get(0));

        dao.add(user2);
        List<User> users2 = dao.getAll();
        assertThat(users2.size(), is(2));
        checkSameUser(user1, users1.get(0));
        checkSameUser(user2, users2.get(1));

        dao.add(user3);
        List<User> users3 = dao.getAll();
        assertThat(users3.size(), is(3));
        checkSameUser(user1, users1.get(0));
        checkSameUser(user2, users2.get(1));
        checkSameUser(user3, users3.get(2));
    }
    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user1.getName(), is(user2.getName()));
        assertThat(user1.getPwd(), is(user2.getPwd()));
    }
    @Test
    public void addDuplicateUserIdExceptionTest() {
        dao.deleteAll();
        assertThat(dao.getCount(), is(0));
        User user1 = new User("inwoo", "인우", "jiw");
        User user2 = new User("amuge", "인우", "jiw");
        User user3 = new User("inwoo", "인우", "jiw");

        dao.add(user1);
        Assertions.assertEquals(dao.getCount(), 1);
        dao.add(user2);
        Assertions.assertEquals(dao.getCount(), 2);
        DuplicateUserIdException thrown = Assertions.assertThrows(DuplicateUserIdException.class, () -> {
            dao.add(user3);
        });
        Assertions.assertEquals(dao.getCount(), 2);
    }
}
