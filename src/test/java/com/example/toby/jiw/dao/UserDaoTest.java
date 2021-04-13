package com.example.toby.jiw.dao;

import com.example.toby.jiw.common.config.AopConfig;
import com.example.toby.jiw.common.exception.DuplicateUserIdException;
import com.example.toby.jiw.common.config.DaoFactory;
import com.example.toby.jiw.domain.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.support.SQLErrorCodeSQLExceptionTranslator;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.SQLException;
import java.util.List;

@SpringBootTest
@ContextConfiguration(classes = { DaoFactory.class, AopConfig.class })
public class UserDaoTest {

    User user1;
    User user2;
    User user3;
    User user4;

    @Autowired
    UserDao dao;

    @Autowired
    DataSource dataSource;

    @BeforeEach
    public void setUp() {
        this.user1 = new User("jiw1", "전인우1", "jiw123!", User.Level.GOLD, 100, 40, "test@email.com");
        this.user2 = new User("jiw2", "전인우2", "jiw123@", User.Level.BASIC, 1, 0, "test@email.com");
        this.user3 = new User("jiw3", "전인우3", "jiw123#", User.Level.SILVER, 55, 10, "test@email.com");
        this.user4 = new User("jiw4", "전인우4", "jiw123$", User.Level.SILVER, 60, 15, "test@email.com");
        dao.deleteAll();
    }

    private void checkSameUser(User user1, User user2) {
        assertThat(user1.getId(), is(user2.getId()));
        assertThat(user1.getName(), is(user2.getName()));
        assertThat(user1.getPwd(), is(user2.getPwd()));
        assertThat(user1.getLevel(), is(user2.getLevel()));
        assertThat(user1.getLogin(), is(user2.getLogin()));
        assertThat(user1.getRecommend(), is(user2.getRecommend()));
    }

    @Test
    @DisplayName("addAndGet 예제")
    public void addAndGet() {
        User addAndGetUser1 = new User("jiw1", "전인우1", "jiw123!", User.Level.GOLD, 100, 40, "test@email.com");
        User addAndGetUser2 = new User("jiw2", "전인우2", "jiw123@", User.Level.GOLD, 100, 40, "test@email.com");

        assertThat(dao.getCount(), is(0));
        dao.add(addAndGetUser1);
        dao.add(addAndGetUser2);
        assertThat(dao.getCount(), is(2));

        User userGet1 = dao.get("jiw1");
        User userGet2 = dao.get("jiw1");
        checkSameUser(userGet1, userGet2);
    }

    @Test
    @DisplayName("count 예제")
    public void count() {
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
    public void getUserFailed() {
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

    @Test
    @DisplayName("예외 확인예제")
    public void addDuplicateUserIdExceptionTest() {
        User exceptionUser1 = new User("jiw1", "전인우1", "jiw123!", User.Level.GOLD, 100, 40, "test@email.com");
        User exceptionUser2 = new User("jiw123", "전인우123", "jiw123!@#", User.Level.GOLD, 100, 40, "test@email.com");
        User exceptionUser3 = new User("jiw1", "전인우1", "jiw123!", User.Level.GOLD, 100, 40, "test@email.com");
        assertThat(dao.getCount(), is(0));
        dao.add(exceptionUser1);
        Assertions.assertEquals(dao.getCount(), 1);
        dao.add(exceptionUser2);
        Assertions.assertEquals(dao.getCount(), 2);
        // DuplicateUserIdException 예외 전환
        Assertions.assertThrows(DuplicateUserIdException.class, () -> {
            dao.addThrownDuplicateUserIdException(exceptionUser3);
        });
        // add 예외
        Assertions.assertThrows(DuplicateKeyException.class, () -> {
            dao.add(exceptionUser3);
        });
        Assertions.assertEquals(dao.getCount(), 2);
    }
    @Test
    @DisplayName("DataSource를 사용 하여 SQLException 전환 예제")
    public void sqlExceptionTranslator() {
        User exceptionUser1 = new User("jiw1", "전인우1", "jiw123!", User.Level.GOLD, 100, 40, "test@email.com");
        User exceptionUser2 = new User("jiw1", "전인우1", "jiw123!", User.Level.GOLD, 100, 40, "test@email.com");
        try {
            dao.add(exceptionUser1);
            dao.add(exceptionUser2);
        } catch (DuplicateKeyException e) {
            SQLException sqlException = (SQLException) e.getRootCause();
            SQLExceptionTranslator set = new SQLErrorCodeSQLExceptionTranslator(this.dataSource);   // 코드를 이용해 SQLException의 전환
            // 에러 메시지를 만들때 사용하는 정보이므로 null로 넣어도 상관없다.
            DuplicateKeyException thrown = Assertions.assertThrows(DuplicateKeyException.class, () -> { // 다른 예외가 터져 상위 예외인 DuplicateKey가 터진다면 정확한 테스트가 맞는지? 그렇기에 class 이름으로 비교하면 정확한 테스트가 될 수 없음
               throw set.translate(null, null, sqlException);
            });
            Assertions.assertEquals(thrown.getClass(), DuplicateKeyException.class);
            Assertions.assertEquals(set.translate(null, null, sqlException).getClass(), DuplicateKeyException.class);
        }
    }
    @Test
    @DisplayName("update 예제")
    public void update() {
        dao.deleteAll();
        dao.add(user1);
        dao.add(user2);

        user1.setName("인우전");
        user1.setPwd("인우전1@#");
        user1.setLevel(User.Level.GOLD);
        user1.setLogin(1000);
        user1.setRecommend(999);
        dao.update(user1);

        User user1update = dao.get(user1.getId());
        User user2same = dao.get(user2.getId());
        checkSameUser(user1, user1update);
        checkSameUser(user2, user2same);
    }

}
