package com.gym.crm.application.service.impl;

import com.gym.crm.application.dao.TrainingDao;
import com.gym.crm.application.model.Training;
import com.gym.crm.application.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingDao trainingDao;

    @Override
    public Training createTraining(Training training) {
        return trainingDao.create(training);
    }

    @Override
    public Training getTrainingById(Long id) {
        return trainingDao.findById(id).orElseThrow(() ->
                new NoSuchElementException(String.format("Trainer with ID %d not found", id)));
    }

    @Override
    public List<Training> getAllTrainings() {
        return trainingDao.findAll();
    }
}
