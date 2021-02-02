package com.example.toby.초난감DAO;

import com.example.toby.초난감DAO.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import java.sql.SQLException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class UserDaoTest3 {

    UserDaoJdbc dao;

    @BeforeEach
    public void setUp() throws SQLException {
        dao = new UserDaoJdbc();
        dao.setDataSource(new SingleConnectionDataSource("jdbc:mysql://localhost:3306/sys?serverTimezone=UTC&characterEncoding=UTF-8", "root", "1234", true));
        dao.deleteAll();
    }

    @Test
    public void add() throws SQLException {
        User user = new User("jiw1", "전인우1", "jiw123!", User.Level.GOLD, 100, 40);
        dao.add(user);
        User user2 = dao.get("id1234");
        assertThat(user.getId(), is(user2.getId()));
    }
}
