package com.example.toby.초난감DAO;

import com.example.toby.초난감DAO.user.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class UserTest {

    User user;

    @BeforeEach
    public void setUp() {
        user = new User();
    }
    @Test
    @DisplayName("user 레벨 업그레이드 테스트")
    public void upgradeLevel() {
        User.Level[] levels = User.Level.values();
        for(User.Level level : levels) {
            if(level.nextLevel() == null) continue;
            user.setLevel(level);
            user.upgradeLevel();
            Assertions.assertEquals(user.getLevel(), level.nextLevel());
        }
    }
    @Test
    @DisplayName("user 레벨 업그레이드 예외 테스트")
    public void cannotUpgradeLevel() {
        User.Level[] levels = User.Level.values();
        for(User.Level level : levels) {
            if(level.nextLevel() != null) continue;
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                user.setLevel(level);
                user.upgradeLevel();
            });

        }
    }
}
