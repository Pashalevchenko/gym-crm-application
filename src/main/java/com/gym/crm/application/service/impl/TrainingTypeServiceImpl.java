package com.gym.crm.application.service.impl;

import com.gym.crm.application.dao.TrainingTypeDao;
import com.gym.crm.application.entity.TrainingType;
import com.gym.crm.application.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeDao trainingTypeDao;

    @Override
    public List<TrainingType> getAllTrainingsType() {
        return trainingTypeDao.findAll();
    }

    @Override
    public TrainingType getByName(String name) {
        return trainingTypeDao.findByName(name)
                .orElseThrow(() -> new NoSuchElementException(String.format("Training type %s not found", name)));
    }
}