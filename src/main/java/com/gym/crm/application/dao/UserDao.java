package com.gym.crm.application.dao;

import com.gym.crm.application.entity.User;

import java.util.Optional;

public interface UserDao {
    User save(User user);

    User update(User user);

    Optional<User> findByUsername(String username);
}
