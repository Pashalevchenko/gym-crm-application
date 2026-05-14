package com.gym.crm.application.dao.impl;

import com.gym.crm.application.config.TransactionHandler;
import com.gym.crm.application.dao.UserDao;
import com.gym.crm.application.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UserDaoImpl implements UserDao {

    private final TransactionHandler transactionHandler;

    @Override
    public User save(User user) {
        transactionHandler.performWithinTransaction(manager -> manager.persist(user));

        return user;
    }

    @Override
    public User update(User user) {
        transactionHandler.performWithinTransaction(manager -> manager.merge(user));

        return user;
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return transactionHandler.performReturningWithinTransaction(manager ->
                manager.createQuery("FROM User u WHERE u.username = :username", User.class)
                        .setParameter("username", username)
                        .getResultStream()
                        .findFirst());
    }
}
