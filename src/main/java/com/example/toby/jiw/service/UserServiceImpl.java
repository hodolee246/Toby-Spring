package com.example.toby.jiw.service;

import com.example.toby.jiw.dao.UserDao;
import com.example.toby.jiw.user.User;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;

import java.util.List;

/**
 * UserService 트랜잭션 코드를 제거한 비즈니스 로직
 */
public class UserServiceImpl implements UserService {

    public static final int MIN_LOGCOUNT_FOR_SILVER = 50;
    public static final int MIN_RECOMMEND_FOR_GOLD = 30;

    UserDao userDao;
    MailSender mailSender;

    public UserServiceImpl(UserDao userDao, MailSender mailSender) {
        this.userDao = userDao;
        this.mailSender = mailSender;
    }

    @Override
    public void add(User user) {
        if(user.getLevel() == null) user.setLevel(User.Level.BASIC);
        userDao.add(user);
    }

    @Override
    public User get(String id) {
        return userDao.get(id);
    }

    @Override
    public List<User> getAll() {
        return userDao.getAll();
    }

    @Override
    public void deleteAll() {
        userDao.deleteAll();
    }

    @Override
    public void update(User user) {
        userDao.update(user);
    }

    public void upgradeLevels() {
        List<User> users = userDao.getAll();
        users.stream().forEach(user -> {
            if(canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
        });
    }

    private void upgradeLevelsInternal() {
        List<User> users = userDao.getAll();
        users.stream().forEach(user -> {
            if(canUpgradeLevel(user)) {
                upgradeLevel(user);
            }
        });
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
        mailSender.send(message);
    }
}