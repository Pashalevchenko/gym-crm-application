package com.gym.crm.application.service;

import com.gym.crm.application.model.Trainer;
import java.util.List;

public interface TrainerService {
    Trainer createTrainer(Trainer trainer);

    Trainer getTrainerById(Long id);

    List<Trainer> getAllTrainers();

    Trainer updateTrainer(Trainer trainer);
}
