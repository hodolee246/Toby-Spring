package com.example.toby.초난감DAO;

import com.example.toby.초난감DAO.daofactory.DaoFactory;
import com.example.toby.초난감DAO.service.UserService;
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
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.toby.초난감DAO.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static com.example.toby.초난감DAO.service.UserService.MIN_RECOMMEND_FOR_GOLD;

@SpringBootTest
@ContextConfiguration(classes = { DaoFactory.class })
public class UserServiceTest {

    @Autowired
    UserService userService;

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
    @DirtiesContext
    @DisplayName("정확한 user 레벨 업그레이드 테스트")    //  user 레벨 업그레이드 테스트를 개선한 업그레이트 테스트
    public void upgradeLevels() throws Exception {
        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        MockMailSender mockMailSender = new MockMailSender();
        userService.setMailSender(mockMailSender);

        userService.upgradeLevels();

        checkLevelUpgraded(users.get(0), false);
        checkLevelUpgraded(users.get(1), true);
        checkLevelUpgraded(users.get(2), false);
        checkLevelUpgraded(users.get(3), true);
        checkLevelUpgraded(users.get(4), false);

        List<String> request = mockMailSender.getRequest();
        Assertions.assertEquals(request.size(), 2);
        Assertions.assertEquals(request.get(0), users.get(1).getEmail());
        Assertions.assertEquals(request.get(3), users.get(4).getEmail());
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
        userDao.deleteAll();
        UserService testUserService = new TestUserService(this.userDao, users.get(3).getId(), dataSource, transactionManager, mailSender);
        for(User user : users) userDao.add(user);

        try {
            testUserService.upgradeLevels();
            Assertions.fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {

        }
        checkLevelUpgraded(users.get(1), false);
    }

    static class TestUserService extends UserService {

        private String id;
        private DataSource dataSource;
        private PlatformTransactionManager transactionManager;
        private MailSender mailSender;

        public TestUserService(UserDao userDao, String id, DataSource dataSource, PlatformTransactionManager transactionManager, MailSender mailSender) {
            super(userDao, dataSource, transactionManager, mailSender);
            this.id = id;
            this.dataSource = dataSource;
            this.transactionManager = transactionManager;
        }

        @Override
        protected void upgradeLevel(User user) {
            if(user.getId().equals(this.id)) throw new TestUserServiceException();
            super.upgradeLevel(user);
        }
    }
    static class TestUserServiceException extends RuntimeException {
    }
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
}