package com.gym.crm.application.dao.impl;

import com.gym.crm.application.config.TransactionHandler;
import com.gym.crm.application.dao.TrainerDaoHibernate;
import com.gym.crm.application.entity.Trainer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TrainerDaoHibernateImpl implements TrainerDaoHibernate {

    private final TransactionHandler transactionHandler;

    @Override
    public Trainer create(Trainer trainer) {
        Trainer created = transactionHandler.performReturningWithinTransaction(session -> {
            session.persist(trainer);
            return trainer;
        });

        log.info("Trainer with id: {} was created", created.getId());
        return created;
    }

    @Override
    public Trainer update(Trainer trainer) {
        Trainer updatedTrainer = transactionHandler.performReturningWithinTransaction(session -> session.merge(trainer));

        log.info("Trainer with id: {} was updated", updatedTrainer.getId());
        return updatedTrainer;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return transactionHandler.performReturningWithinTransaction(session ->
                Optional.ofNullable(session.get(Trainer.class, id)));
    }

    @Override
    public Optional<Trainer> findByUsername(String username) {
        return transactionHandler.performReturningWithinTransaction(session ->
                session.createQuery("from Trainer t where t.user.username = :username", Trainer.class)
                        .setParameter("username", username)
                        .uniqueResultOptional());
    }

    @Override
    public List<Trainer> findAll() {
        return transactionHandler.performReturningWithinTransaction(session ->
                session.createQuery("from Trainer", Trainer.class)
                        .getResultList());
    }
}