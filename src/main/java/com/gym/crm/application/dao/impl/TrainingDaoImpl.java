package com.gym.crm.application.dao.impl;

import com.gym.crm.application.aspect.annotation.Transactional;
import com.gym.crm.application.config.TransactionHandler;
import com.gym.crm.application.dao.TrainingDao;
import com.gym.crm.application.entity.Training;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TrainingDaoImpl implements TrainingDao {

    private final TransactionHandler transactionHandler;

    @Override
    @Transactional
    public Training create(Training training) {
        Training created = transactionHandler.performReturningWithinTransaction(session -> {
            session.persist(training);
            return training;
        });

        log.info("Training with id: {} was created", created.getId());
        return created;
    }


    @Override
    public Optional<Training> findById(Long id) {
        return transactionHandler.performReturningWithinTransaction(session ->
                Optional.ofNullable(session.get(Training.class, id)));
    }

    @Override
    public List<Training> findAll() {
        return transactionHandler.performReturningWithinTransaction(session ->
                session.createQuery("from Training", Training.class).getResultList());
    }
}