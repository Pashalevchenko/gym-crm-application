package com.gym.crm.application.service.impl;

import com.gym.crm.application.dao.TrainingTypeDao;
import com.gym.crm.application.entity.TrainingType;
import com.gym.crm.application.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {

    TrainingTypeDao trainingTypeDao;

    @Override
    public List<TrainingType> getAllTrainingsType() {
        return trainingTypeDao.findAll();
    }
}