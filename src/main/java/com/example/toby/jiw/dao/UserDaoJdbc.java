package com.example.toby.jiw.dao;

import com.example.toby.jiw.Exception.DuplicateUserIdException;
import com.example.toby.jiw.user.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

public class UserDaoJdbc implements UserDao {

    private Map<String, String> sqlMap;

    private JdbcTemplate jdbcTemplate;
    private RowMapper<User> userRowMapper = (rs, rowNumber) -> {
        User user = new User();
        user.setId(rs.getString("id"));
        user.setName(rs.getString("name"));
        user.setPwd(rs.getString("pwd"));
        user.setLevel(User.Level.valueOf(rs.getInt("level")));
        user.setLogin(rs.getInt("login"));
        user.setRecommend(rs.getInt("recommend"));
        user.setEmail(rs.getString("email"));

        return user;
    };

    public UserDaoJdbc() {}

    public UserDaoJdbc(Map<String, String> sqlMap, DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        this.sqlMap = sqlMap;
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setSqlMap(Map<String, String> sqlMap) {
        this.sqlMap = sqlMap;
    }

    @Override
    public void add(final User user) throws DuplicateUserIdException {
        this.jdbcTemplate.update(this.sqlMap.get("add"), user.getId(), user.getName(), user.getPwd(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail()); // add method none throw SQLException
    }
    @Override
    public User get(String id) {
        return this.jdbcTemplate.queryForObject(this.sqlMap.get("get"), new Object[]{id}, this.userRowMapper);
    }

    @Override
    public List<User> getAll() {
        return this.jdbcTemplate.query(this.sqlMap.get("getAll"), this.userRowMapper);
    }

    @Override
    public void deleteAll() {
        this.jdbcTemplate.update(this.sqlMap.get("deleteAll"));
    }

    @Override
    public int getCount() {
        return this.jdbcTemplate.queryForObject(this.sqlMap.get("getCount"), Integer.class);
    }

    @Override
    public void update(User user1) {
        this.jdbcTemplate.update(this.sqlMap.get("update"),
                user1.getName(), user1.getPwd(), user1.getLevel().intValue(), user1.getLogin(), user1.getRecommend(), user1.getEmail(), user1.getId()
        );
    }
}
