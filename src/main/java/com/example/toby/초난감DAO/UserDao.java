package com.example.toby.초난감DAO;

import com.example.toby.초난감DAO.Exception.DuplicateUserIdException;
import com.example.toby.초난감DAO.Exception.MysqlErrorNumbers;
import com.example.toby.초난감DAO.user.User;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.sql.*;
import java.util.List;

public class UserDao {

    private JdbcTemplate jdbcTemplate;
    private RowMapper<User> userRowMapper = (rs, rowNumber) -> {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPwd(rs.getString("pwd"));

        return user;
    };

    public UserDao() {}

    public UserDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void add(final User user) throws DuplicateUserIdException {
        try {
            // add error 고유키 중복 발생!
            this.jdbcTemplate.update("insert into users(id, name, pwd) values(?,?,?)", user.getId(), user.getName(), user.getPwd()); // add method none throw SQLException
        } catch (DuplicateKeyException e) {
            // 일단 임시로 trace 출력 후 새롭게 포장하여 던져준다.
            throw new DuplicateUserIdException(e);
        }
    }
    public User get(String id) throws SQLException {
        return this.jdbcTemplate.queryForObject("select * from users where id = ?", new Object[]{id}, this.userRowMapper);
    }
    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by id", this.userRowMapper);
    }
    public void deleteAll() {
        this.jdbcTemplate.update("delete from users");
    }
    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
    }
}
