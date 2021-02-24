package com.example.toby.초난감DAO.service;

import com.example.toby.초난감DAO.user.User;

public interface UserLevelUpgradePolicy {
    boolean canUpgradeLevel(User user);
    void upgradeLevel(User user);
}
