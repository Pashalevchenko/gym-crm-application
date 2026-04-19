package com.gym.crm.application.service.impl;

import com.gym.crm.application.dao.TrainerDao;
import com.gym.crm.application.model.Trainer;
import com.gym.crm.application.service.TrainerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerDao trainerDao;

    @Override
    public Trainer createTrainer(Trainer trainer) {
        return trainerDao.create(trainer);
    }

    @Override
    public Trainer getTrainerById(Long id) {
        return trainerDao.findById(id).orElseThrow(() ->
                new NoSuchElementException(String.format("Trainer with ID %d not found", id)));
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return trainerDao.findAll();
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        return trainerDao.update(trainer);
    }
}
