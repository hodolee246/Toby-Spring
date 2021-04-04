package com.example.toby.jiw.service;

import com.example.toby.jiw.dao.UserDao;
import com.example.toby.jiw.config.AopConfig;
import com.example.toby.jiw.config.DaoFactory;
import com.example.toby.jiw.config.FactoryBean;
import com.example.toby.jiw.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.example.toby.jiw.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static com.example.toby.jiw.service.UserServiceImpl.MIN_RECOMMEND_FOR_GOLD;

/** 스텁과 목을 이용한 비즈니스로직 통합, 단위 테스트
 *
 */
@SpringBootTest
@ContextConfiguration(classes = {FactoryBean.class, DaoFactory.class, AopConfig.class})
public class UserServiceTest {

    @Autowired UserService userService;
    @Autowired UserService testUserService;
    @Autowired UserDao userDao;
    @Autowired DataSource dataSource;
//    @Autowired DataSourceTransactionManager transactionManager;
    @Autowired MailSender mailSender;
    @Autowired ApplicationContext context;
    @Autowired PlatformTransactionManager transactionManager;

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
    @DisplayName("upgradeLevel() / user 레벨 업그레이드 테스트")    // 업그레이드가 되었는지 확인하려는 코드인지? / 업그레이드가 안되었는지 확인하려는 테스트 코드인지 분간이 힘들다.
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
    @DisplayName("upgradeLevels() / 정확한 user 레벨 업그레이드 테스트")    //  user 레벨 업그레이드 테스트를 개선한 업그레이드 테스트
    public void upgradeLevels() throws Exception {
        UserDao mockUserDao = Mockito.mock(UserDao.class);
        // getAll() 호출 시 users 반환
        Mockito.when(mockUserDao.getAll()).thenReturn(this.users);
        MailSender mockMailSender = Mockito.mock(MailSender.class);

        UserServiceImpl userServiceImpl = new UserServiceImpl(mockUserDao, mockMailSender);
        userServiceImpl.upgradeLevels();

        Mockito.verify(mockUserDao, Mockito.times(2)).update(Mockito.any(User.class));
        Mockito.verify(mockUserDao, Mockito.times(2)).update(Mockito.any(User.class));
        Mockito.verify(mockUserDao).update(users.get(1));
        Assertions.assertEquals(users.get(1).getLevel(), User.Level.SILVER);
        Mockito.verify(mockUserDao).update(users.get(3));
        Assertions.assertEquals(users.get(3).getLevel(), User.Level.GOLD);

        ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
        //                                                  파라미터를 정밀하게 검사하기 위해 캡처할 수 도 있다.
        Mockito.verify(mockMailSender, Mockito.times(2)).send(mailMessageArg.capture());
        List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
        Assertions.assertEquals(mailMessages.get(0).getTo()[0], users.get(1).getEmail());
        Assertions.assertEquals(mailMessages.get(1).getTo()[0], users.get(3).getEmail());
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
    @DisplayName("add() / user 추가 기본 레벨 테스트")
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
    // bean 등록 에러로 인한 미수행 485p ~ 488p
    @Test
    @DisplayName("upgradeAllOrNothing() / 예외 발생 시 작업 취소 여부 테스트")
    public void upgradeAllOrNothing() throws Exception {
        userDao.deleteAll();
        for(User user : users) userDao.add(user);

        try {
            userService.upgradeLevels(); // bean 등록 에러로 인한 미수행
            Assertions.fail("TestUserServiceException expected");
        } catch (TestUserServiceException e) {

        }
        checkLevelUpgraded(users.get(1), false);
    }

    @Test
    public void transactionSync1() {    // ch6 aop @Transactional 을 사용한 DB테스트 시 H2는 실패하는 문제발생
        userService.deleteAll();
        Assertions.assertEquals(userDao.getCount(), 0);

        DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);

        userService.add(users.get(0));
        userService.add(users.get(1));
        Assertions.assertEquals(userDao.getCount(), 2);

        transactionManager.rollback(txStatus);
        Assertions.assertEquals(userDao.getCount(), 0);
    }

    @Test
    public void transactionSync2() {    // ch6 aop @Transactional 을 사용한 DB테스트 시 H2는 실패하는 문제발생
        userService.deleteAll();
        Assertions.assertEquals(userDao.getCount(), 0);

        DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);

        userService.add(users.get(0));
        userService.add(users.get(1));
        Assertions.assertEquals(userDao.getCount(), 2);

        transactionManager.commit(txStatus);
        Assertions.assertEquals(userDao.getCount(), 2);
    }

    @Test
    public void transactionSync3() {    // ch6 aop @Transactional 을 사용한 DB테스트 시 H2는 실패하는 문제발생
        DefaultTransactionDefinition txDefinition = new DefaultTransactionDefinition();
        TransactionStatus txStatus = transactionManager.getTransaction(txDefinition);

        try {
            userService.deleteAll();
            userService.add(users.get(0));
            userService.add(users.get(1));
        } finally {
            transactionManager.rollback(txStatus);
        }
    }

    @Test
    @Transactional(readOnly = true)
    public void transactionSync4() {    // ch6 aop @Transactional 을 사용한 DB테스트 시 H2는 실패하는 문제발생
        userService.deleteAll();
    }

    /**
     * 자동 프록시 생성기 테스트용 TestUserServiceImpl
     */
    static class TestUserServiceImpl extends UserServiceImpl {

        private String id = "madnite1";

        public TestUserServiceImpl(UserDao userDao, MailSender mailSender) {
            super(userDao, mailSender);
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

        private List<User> users;                       // 레벨 업그레이드 후보보 ser 오브젝트 목록
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