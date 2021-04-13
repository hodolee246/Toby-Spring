package com.example.toby.jiw.service;

import com.example.toby.jiw.domain.user.User;

public interface UserLevelUpgradePolicy {
    boolean canUpgradeLevel(User user);
    void upgradeLevel(User user);
}
