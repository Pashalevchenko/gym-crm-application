package com.gym.crm.application.dao.impl;

import com.gym.crm.application.dao.TrainerDao;
import com.gym.crm.application.model.Trainer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
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
        log.info("Trainer {} {} was created", trainer.getFirstName(), trainer.getLastName());

        return trainer;
    }

    @Override
    public Trainer update(Trainer trainer) {
        Long id = trainer.getId();

        if (!storage.containsKey(id)) {
            log.error("Cannot update Trainer: ID {} not found in storage", id);

            throw new RuntimeException(String.format("Cannot update Trainer: ID %d not found in storage", id));
        }

        storage.put(id, trainer);
        log.info("Trainer with id: {} was update", id);

        return trainer;
    }

    @Override
    public Optional<Trainer> findById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    @Override
    public List<Trainer> findAll() {
        return new ArrayList<>(storage.values());
    }
}