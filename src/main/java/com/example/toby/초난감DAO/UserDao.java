package com.example.toby.초난감DAO;

import com.example.toby.초난감DAO.user.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;

import javax.sql.DataSource;
import java.sql.*;

public class UserDao {

    private JdbcContext jdbcContext;
    private DataSource dataSource;

    public UserDao() {}

    public UserDao(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcContext = new JdbcContext(dataSource);
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcContext = new JdbcContext(dataSource);
    }

    public void add(final User user) throws SQLException {
//        class AddStatement implements StatementStrategy {   // 중첩 클래스
//            @Override
//            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//                PreparedStatement ps = c.prepareStatement("insert into users(id, name, pwd) values(?, ?, ?)");
//                ps.setString(1, user.getId());
//                ps.setString(2, user.getName());
//                ps.setString(3, user.getPwd());
//                return ps;
//            }
//        }
//        StatementStrategy st = new StatementStrategy() { // 익명 내부 클래스
//            @Override
//            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//                PreparedStatement ps = c.prepareStatement("insert into users(id, name, pwd) values(?, ?, ?)");
//                ps.setString(1, user.getId());
//                ps.setString(2, user.getName());
//                ps.setString(3, user.getPwd());
//                return ps;
//            }
//        };
//        jdbcContextWithStatementStrategy(new StatementStrategy() { // 메소드 파라미터로 이전한 익명 내부 클래스
//            @Override
//            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//                PreparedStatement ps = c.prepareStatement("insert into users(id, name, pwd) values(?, ?, ?)");
//                ps.setString(1, user.getId());
//                ps.setString(2, user.getName());
//                ps.setString(3, user.getPwd());
//                return ps;
//            };
//        });
        // 메소드 파라미터로 이전한 익명 내부 클래스
        this.jdbcContext.workWithStatementStrategy(c -> { // 메소드 파라미터로 이전한 익명 내부 클래스(람다)
            PreparedStatement ps = c.prepareStatement("insert into users(id, name, pwd) values(?, ?, ?)");
            ps.setString(1, user.getId());
            ps.setString(2, user.getName());
            ps.setString(3, user.getPwd());
            return ps;
        });
    }
    public User get(String id) throws SQLException {
        Connection c = dataSource.getConnection();
        PreparedStatement ps = c.prepareStatement(
                "select * from users where id = ?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();

        User user = null;
        if(rs.next()) {
            user = new User();
            user.setId(rs.getString("id"));
            user.setName(rs.getString("name"));
            user.setPwd(rs.getString("pwd"));
        }
        rs.close();
        ps.close();
        c.close();

        if(user == null) throw new EmptyResultDataAccessException(1);

        return user;
    }
    public void deleteAll() throws SQLException {
//        this.jdbcContext.executeSql("delete from users"); // jdbcTemplate 로 변경됨
        this.jdbcTemplate.update(
                new PreparedStatementCreator() {
                    @Override
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        return connection.prepareStatement("delete from users");
                    }
                }
        );
    }
//        StatementStrategy st = new DeleteAllStatement(); // 어떤 전략을 사용할지 알고있음
//        ps = st.makePreparedStatement(c);

//        this.jdbcContext.workWithStatementStrategy(new StatementStrategy() {  // 메소드 파라미터로 이전한 익명 내부 클래스
//            @Override
//            public PreparedStatement makePreparedStatement(Connection c) throws SQLException {
//                return c.prepareStatement("delete from users");
//            }
//        });
//        this.jdbcContext.workWithStatementStrategy(c -> c.prepareStatement("delete from users")); //메소드 파라미터로 이전한 익명 내부 클래스(람다)
    public int getCount() throws SQLException {
        Connection c = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            c = dataSource.getConnection();
            ps = c.prepareStatement("select count(*) from users");
            rs = ps.executeQuery();
            rs.next();
            return rs.getInt(1);
        } catch (SQLException e) {
            throw e;
        } finally {
            if(rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {}
            }
            if(ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {}
            }
            if(c != null) {
                try {
                    c.close();
                } catch (SQLException e) {}
            }
        }
    }

//    // 메소드로 분리한 try, catch. finally 코드
//    public void  jdbcContextWithStatementStrategy(StatementStrategy stmt) throws SQLException {
//        Connection c = null;
//        PreparedStatement ps = null;
//
//        try {
//            c= dataSource.getConnection();
//            ps = stmt.makePreparedStatement(c);
//            ps.executeUpdate();
//        } catch (SQLException e) {
//            throw e;
//        } finally {
//            if(ps != null) {
//                try {
//                    ps.close();
//                } catch (SQLException e) {
//                }
//            }
//            if(c != null) {
//                try {
//                    c.close();
//                } catch (SQLException e) {
//                }
//            }
//        }
//    }

}
