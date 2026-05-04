package com.gym.crm.application.service.impl;

import com.gym.crm.application.dao.TrainingDaoHibernate;
import com.gym.crm.application.entity.Training;
import com.gym.crm.application.service.TrainingService;
import com.gym.crm.application.validation.TrainingValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainingServiceImpl implements TrainingService {

    private final TrainingDaoHibernate trainingDao;
    private final TrainingValidator trainingValidator;

    @Override
    public Training createTraining(Training training) {
        trainingValidator.validateForCreate(training);

        Training created = trainingDao.create(training);

        log.info("Training created with id: {}", created.getId());
        return created;
    }

    @Override
    public Training getTrainingById(Long id) {
        return trainingDao.findById(id).orElseThrow(() ->
                new NoSuchElementException("Training with ID " + id + " not found"));
    }

    @Override
    public List<Training> getAllTrainings() {
        return trainingDao.findAll();
    }
}