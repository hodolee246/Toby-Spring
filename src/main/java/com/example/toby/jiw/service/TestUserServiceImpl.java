package com.example.toby.jiw.service;

import com.example.toby.jiw.dao.UserDao;
import com.example.toby.jiw.domain.user.User;
import org.springframework.mail.MailSender;
/**
 * 자동 프록시 생성기 테스트용 TestUserServiceImpl
 */
public class TestUserServiceImpl extends UserServiceImpl{

    private String id = "madnite1";

    public TestUserServiceImpl(UserDao userDao, MailSender mailSender) {
        super(userDao, mailSender);
    }

    @Override
    protected void upgradeLevel(User user) {
        if(user.getId().equals(this.id)) throw new TestUserServiceException();
        super.upgradeLevel(user);
    }

    /**
     * 테스트용 Exception
     */
    static class TestUserServiceException extends RuntimeException {
    }
}
