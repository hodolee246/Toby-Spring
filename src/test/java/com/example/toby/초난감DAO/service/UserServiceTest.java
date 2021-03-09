package com.example.toby.초난감DAO.service;

import com.example.toby.초난감DAO.UserDao;
import com.example.toby.초난감DAO.daofactory.DaoFactory;
import com.example.toby.초난감DAO.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.toby.초난감DAO.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static com.example.toby.초난감DAO.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

@SpringBootTest
@ContextConfiguration(classes = { DaoFactory.class })
public class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    UserServiceImpl userServiceImpl;

    @Autowired
    UserDao userDao;

    @Autowired
    DataSource dataSource;

    @Autowired
    DataSourceTransactionManager transactionManager;

    @Autowired
    MailSender mailSender;

    List<User> users;

    @BeforeEach
    public void setUp() {
        users = Arrays.asList(
            new User("InWooJeon1", "전인우1", "p1", User.Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "test@email.com"),
            new User("InWooJeon2", "전인우2", "p2", User.Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "test@email.com"),
            new User("InWooJeon3", "전인우3", "p3", User.Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD-1, "test@email.com"),
            new User("InWooJeon4", "전인우4", "p4", User.Level.SILVER, 60, MIN_RECOMMEND_FOR_GOLD, "test@email.com"),
            new User("InWooJeon5", "전인우5", "p5", User.Level.GOLD, 100, Integer.MAX_VALUE, "test@email.com")
        );
    }
    @Test
    @DisplayName("user 레벨 업그레이드 테스트")    // 업그레이드가 되었는지 확인하려는 코드인지? / 업그레이드가 안되었는지 확인하려는 테스트 코드인지 분간이 힘들다.
    public void upgradeLevel() throws Exception {
        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        userService.upgradeLevels();

        checkLevel(users.get(0), User.Level.BASIC);
        checkLevel(users.get(1), User.Level.SILVER);
        checkLevel(users.get(2), User.Level.SILVER);
        checkLevel(users.get(3), User.Level.GOLD);
        checkLevel(users.get(4), User.Level.GOLD);
    }
    private void checkLevel(User user, User.Level expectedLevel) {
        User userUpdate = userDao.get(user.getId());
        Assertions.assertEquals(userUpdate.getLevel(), expectedLevel);
    }
    @Test
    @DisplayName("정확한 user 레벨 업그레이드 테스트")    //  user 레벨 업그레이드 테스트를 개선한 업그레이드 테스트
    public void upgradeLevels() throws Exception {
        MockUserDao mockUserDao = new MockUserDao(this.users);
        MockMailSender mockMailSender = new MockMailSender();
        UserServiceImpl userServiceImpl = new UserServiceImpl(mockUserDao, mockMailSender);

        userServiceImpl.upgradeLevels();

        List<User> updated = mockUserDao.getUpdated();
        Assertions.assertEquals(updated.size(), 2);
        checkUserAndLevel(updated.get(0), "InWooJeon2", User.Level.SILVER);
        checkUserAndLevel(updated.get(1), "InWooJeon4", User.Level.GOLD);

        userService.upgradeLevels();

        List<String> request = mockMailSender.getRequest();
        Assertions.assertEquals(request.size(), 2);
        Assertions.assertEquals(request.get(0), users.get(1).getEmail());
        Assertions.assertEquals(request.get(1), users.get(3).getEmail());
    }

    private void checkUserAndLevel(User updated, String expectedId, User.Level expectedLevel) {
        Assertions.assertEquals(updated.getId(), expectedId);
        Assertions.assertEquals(updated.getLevel(), expectedLevel);
    }

    private void checkLevelUpgraded(User user, boolean upgraded) {
        User userUpdate = userDao.get(user.getId());
        if(upgraded) {
            Assertions.assertEquals(userUpdate.getLevel(), user.getLevel().nextLevel());
        } else {
            Assertions.assertEquals(userUpdate.getLevel(), user.getLevel());
        }
    }

    @Test
    @DisplayName("user 추가 기본 레벨 테스트")
    public void add() {
        userDao.deleteAll();

        User userWithLevel = users.get(4);
        User userWithoutLevel = users.get(0);
        userWithoutLevel.setLevel(null);;

        userService.add(userWithLevel);
        userService.add(userWithoutLevel);

        User userWithLevelRead = userDao.get(userWithLevel.getId());
        User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());

        Assertions.assertEquals(userWithLevelRead.getLevel(), userWithLevel.getLevel());
        Assertions.assertEquals(userWithoutLevelRead.getLevel(), userWithoutLevel.getLevel());
    }
    @Test
    @DisplayName("예외 발생 시 작업 취소 여부 테스트")
    public void upgradeAllOrNothing() throws Exception {
        TestUserService testUserService = new TestUserService(userDao, mailSender, users.get(3).getId());

        UserServiceTx txUserService = new UserServiceTx(transactionManager, testUserService);

        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        try {
            txUserService.upgradeLevels();
            Assertions.fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {

        }
        checkLevelUpgraded(users.get(1), false);
    }

    static class TestUserService extends UserServiceImpl {

        private String id;

        public TestUserService(UserDao userDao, MailSender mailSender, String id) {
            super(userDao, mailSender);
            this.id = id;
        }

        @Override
        protected void upgradeLevel(User user) {
            if(user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }

    /**
     * 테스트용 Exception
     */
    static class TestUserServiceException extends RuntimeException {
    }

    /**
     * 테스트용 MockMailSender 클래스
     */
    static class MockMailSender implements MailSender {

        private List<String> request = new ArrayList<>();   // UserServiceTest 로 부터 전송받은 메일 주소를 저장해두고 읽을 수 있게 사용하는 변수

        public List<String> getRequest() {
            return request;
        }

        @Override
        public void send(SimpleMailMessage simpleMailMessage) throws MailException {
            request.add(simpleMailMessage.getTo()[0]);  // 전송 요청을 받은 첫 번째 수신자 메일 주소만 저장
        }

        @Override
        public void send(SimpleMailMessage... simpleMailMessages) throws MailException {
        }
    }
    /**
     * 테스트용 MockUserDao
     */
    static class MockUserDao implements UserDao {

        private List<User> users;                       // 레벨 업그레이드 후부 User 오브젝트 목록
        private List<User> updated = new ArrayList<>(); // 업그레이드 대상 오브젝트를 저장해둘 목록

        private MockUserDao(List<User> users) {
            this.users = users;
        }

        public List<User> getUpdated() {
            return this.updated;
        }

        public List<User> getAll() {
            return this.users;
        }
        public void update(User user) {
            updated.add(user);
        }

        @Override
        public void add(User user) { throw new UnsupportedOperationException(); }
        @Override
        public User get(String id) { throw new UnsupportedOperationException(); }
        @Override
        public void deleteAll() { throw new UnsupportedOperationException(); }
        @Override
        public int getCount() { throw new UnsupportedOperationException(); }
    }
}