package com.example.toby.jiw.service;

import com.example.toby.jiw.user.User;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.List;

/**
 * UserServie 트랜잭션 로직을 분리한 클래스
 */
public class UserServiceTx implements UserService {

    PlatformTransactionManager transactionManager;
    UserService userService;

    public UserServiceTx(PlatformTransactionManager transactionManager, UserService userService) {
        this.transactionManager = transactionManager;
        this.userService = userService;
    }

    public void add(User user) {
        userService.add(user);
    }

    @Override
    public void deleteAll() {

    }

    @Override
    public void update(User user) {

    }

    public void upgradeLevels() {
        TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
        try {
            userService.upgradeLevels();
            this.transactionManager.commit(status);
        } catch (Exception e) {
            this.transactionManager.rollback(status);
            throw e;
        }
    }

    @Override
    public User get(String id) {
        return null;
    }

    @Override
    public List<User> getAll() {
        return null;
    }
}
