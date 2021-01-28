package com.example.toby.초난감DAO;

import com.example.toby.초난감DAO.Exception.DuplicateUserIdException;
import com.example.toby.초난감DAO.user.User;
import org.springframework.dao.DuplicateKeyException;

import java.util.List;

public interface UserDao {
    void add(User user);
    User get(String id);
    List<User> getAll();
    void deleteAll();
    int getCount();
    // 예외전환용 디폴트 메소드
    default void addThrownDuplicateUserIdException(User user) {
        try {
            add(user);
        } catch (DuplicateKeyException e) {
            throw new DuplicateUserIdException(e);
        }
    }
}
