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
    }

    @Override
    public Training create(Training training) {

        if (!storage.isEmpty()) {

            long maxId = storage.keySet().stream()
                    .max(Long::compare)
                    .orElse(0L);
            idGenerator.set(maxId);
        }

        storage.put(idGenerator.incrementAndGet(), training);

        return training;
    }

    @Override
    public Training update(Training training) {

        Long key = getKeyByName(training.getTrainingName());

        storage.put(key, training);

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

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }

    private Long getKeyByName(String TrainingName){

        return storage.entrySet().stream()
                .filter(entry -> {
                    Object value = entry.getValue();

                    if (value instanceof Map) {
                        return TrainingName.equals(((Map<?, ?>) value).get("trainingName"));
                    }

                    if (value instanceof Training) {
                        return ((Training) value).getTrainingName().equals(TrainingName);
                    }

                    return false;
                })
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Training not found for update: " + TrainingName));
    }
}