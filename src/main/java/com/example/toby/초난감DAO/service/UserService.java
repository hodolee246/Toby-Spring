package com.example.toby.초난감DAO.service;

import com.example.toby.초난감DAO.UserDao;
import com.example.toby.초난감DAO.user.User;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.sql.DataSource;
import java.util.List;

public class UserService {

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    private final UserDao userDao;
    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;
    private MailSender mailSender;

    public UserService(UserDao userDao, DataSource dataSource, PlatformTransactionManager transactionManager, MailSender mailSender) {
        this.userDao = userDao;
        this.dataSource = dataSource;
        this.transactionManager = transactionManager;
        this.mailSender = mailSender;
    }

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void upgradeLevels() throws Exception {
        PlatformTransactionManager transactionManager = new DataSourceTransactionManager(dataSource);   // JDBC 트랜잭션 추상 오브젝트 생성
        TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            List<User> users = userDao.getAll();
            users.stream().forEach(user -> {
                if(canUpgradeLevel(user)) {
                    upgradeLevel(user);
                }
            });
            transactionManager.commit(status);  // 트랜잭션 커밋
        } catch (RuntimeException e) {
            transactionManager.rollback(status);    // 트랜잭션 커밋
            throw e;
        }

    }

    public void add(User user) {
        if(user.getLevel() == null) user.setLevel(User.Level.BASIC);
        userDao.add(user);
    }

    protected void upgradeLevel(User user) {
        user.upgradeLevel();
        userDao.update(user);
        sendUpgradeEmail(user);
    }

    private boolean canUpgradeLevel(User user) {
        User.Level currentLevel = user.getLevel();
        switch (currentLevel) {
            case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
            case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
            case GOLD: return false;
            default: throw new IllegalArgumentException("Unknown Level : '" + currentLevel + "'");
        }
    }

    private void sendUpgradeEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setFrom("useradmin@ksug.com");
        message.setSubject("등급 업그레이드 안내");
        message.setText(user.getName() + "님의 등급이 " + user.getLevel().name() + "으로 올라갔습니다.");

        this.mailSender.send(message);
    }
}