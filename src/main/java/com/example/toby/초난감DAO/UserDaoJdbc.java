package com.example.toby.초난감DAO;

import com.example.toby.초난감DAO.Exception.DuplicateUserIdException;
import com.example.toby.초난감DAO.user.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.util.List;

public class UserDaoJdbc implements UserDao {

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

    public UserDaoJdbc(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void add(final User user) throws DuplicateUserIdException {
        this.jdbcTemplate.update("insert into users(id, name, pwd, level, login, recommend, email) values(?, ?, ?, ?, ?, ?, ?)", user.getId(), user.getName(), user.getPwd(), user.getLevel().intValue(), user.getLogin(), user.getRecommend(), user.getEmail()); // add method none throw SQLException
    }
    @Override
    public User get(String id) {
        return this.jdbcTemplate.queryForObject("select * from users where id = ?", new Object[]{id}, this.userRowMapper);
    }

    @Override
    public List<User> getAll() {
        return this.jdbcTemplate.query("select * from users order by id", this.userRowMapper);
    }

    @Override
    public void deleteAll() {
        this.jdbcTemplate.update("delete from users");
    }

    @Override
    public int getCount() {
        return this.jdbcTemplate.queryForObject("select count(*) from users", Integer.class);
    }

    @Override
    public void update(User user1) {
        this.jdbcTemplate.update(
          "update users set name = ?, pwd = ?, level = ?, login = ?, recommend = ?, email = ? where id = ?",
                user1.getName(), user1.getPwd(), user1.getLevel().intValue(), user1.getLogin(), user1.getRecommend(), user1.getEmail(), user1.getId()
        );
    }
}
