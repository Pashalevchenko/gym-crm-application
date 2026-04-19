package com.gym.crm.application.service;

import com.gym.crm.application.model.Trainee;
import java.util.List;

public interface TraineeService {
    Trainee createTrainee(Trainee trainee);

    Trainee getTraineeById(Long id);

    List<Trainee> getAllTrainees();

    Trainee updateTrainee(Trainee trainee);

    void deleteTrainee(Long id);
}
