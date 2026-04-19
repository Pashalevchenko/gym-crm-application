package com.gym.crm.application.service.impl;

import com.gym.crm.application.dao.TraineeDao;
import com.gym.crm.application.model.Trainee;
import com.gym.crm.application.service.TraineeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private final TraineeDao traineeDao;

    @Override
    public Trainee createTrainee(Trainee trainee) {
        return traineeDao.create(trainee);
    }

    @Override
    public Trainee getTraineeById(Long id) {
        return traineeDao.findById(id).orElseThrow(() ->
                new NoSuchElementException(String.format("Trainee with ID %d not found", id)));
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return traineeDao.findAll();
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        return traineeDao.update(trainee);
    }

    @Override
    public void deleteTrainee(Long id) {
        traineeDao.delete(id);
    }
}
