package com.gym.crm.application.dao.impl;

import com.gym.crm.application.dao.TrainerDao;
import com.gym.crm.application.model.Trainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class TrainerDaoImpl implements TrainerDao {

    private final Map<Long, Trainer> storage;

    @Autowired
    public TrainerDaoImpl(Map<Long, Trainer> trainerStorage) {
        this.storage = trainerStorage;
    }

    @Override
    public Trainer create(Trainer trainer) {
        storage.put(trainer.getId(), trainer);
        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {

        Long id = trainer.getId();

        if (storage.containsKey(id)) {

            storage.put(id, trainer);

            return trainer;
        } else {
            throw new RuntimeException("Cannot update Trainer: ID " + id + " not found in storage.");
        }
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Trainer> findAll() {
        return new ArrayList<>(storage.values());
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }
}