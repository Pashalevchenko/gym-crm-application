package com.gym.crm.application.dao.impl;

import com.gym.crm.application.dao.TrainingDao;
import com.gym.crm.application.model.Training;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class TrainingDaoImpl implements TrainingDao {

    private final Map<Long, Training> storage;
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Autowired
    public TrainingDaoImpl(Map<Long, Training> trainingStorage) {
        this.storage = trainingStorage;
        syncStorageId();
    }

    @Override
    public Training create(Training training) {
        storage.put(idGenerator.incrementAndGet(), training);

        return training;
    }

    @Override
    public Optional<Training> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Training> findAll() {
        return new ArrayList<>(storage.values());
    }

    private void syncStorageId(){
        if (!storage.isEmpty()) {
            long maxId = storage.keySet().stream()
                    .max(Long::compare)
                    .orElse(0L);

            idGenerator.set(maxId);
        }
    }
}