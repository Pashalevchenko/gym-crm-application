package com.gym.crm.application.dao.impl;

import com.gym.crm.application.dao.TraineeDao;
import com.gym.crm.application.model.Trainee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TraineeDaoImpl implements TraineeDao {

    private final Map<Long, Trainee> storage;

    @Autowired
    public TraineeDaoImpl(Map<Long, Trainee> traineeStorage) {
        this.storage = traineeStorage;
    }

    @Override
    public Trainee create(Trainee trainee) {
        storage.put(trainee.getId(), trainee);
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        Long id = trainee.getId();

        if (!storage.containsKey(id)) {
            throw new RuntimeException(String.format("Cannot update Trainee: ID %d not found in storage", id));
        }

        storage.put(id, trainee);
        return trainee;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Trainee> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }
}