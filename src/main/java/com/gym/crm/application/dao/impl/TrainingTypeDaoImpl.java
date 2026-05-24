package com.gym.crm.application.dao.impl;

import com.gym.crm.application.config.TransactionHandler;
import com.gym.crm.application.dao.TrainingTypeDao;
import com.gym.crm.application.entity.TrainingType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TrainingTypeDaoImpl implements TrainingTypeDao {

    private final TransactionHandler transactionHandler;

    @Override
    public List<TrainingType> findAll() {
        return transactionHandler.performReturningWithinTransaction(session ->
                session.createQuery("from TrainingType", TrainingType.class).getResultList());
    }

    @Override
    public Optional<TrainingType> findByName(String name) {
        return transactionHandler.performReturningWithinTransaction(session ->
                session.createQuery("""
                    from TrainingType tt
                    where tt.trainingTypeName = :name
                    """, TrainingType.class)
                        .setParameter("name", name)
                        .uniqueResultOptional());
    }
}