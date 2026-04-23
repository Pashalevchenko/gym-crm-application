package com.gym.crm.application.service.impl;

import com.gym.crm.application.dao.TraineeDao;
import com.gym.crm.application.model.Trainee;
import com.gym.crm.application.service.ProfileService;
import com.gym.crm.application.service.TraineeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private final TraineeDao traineeDao;
    private final ProfileService profileService;

    @Override
    public Trainee createTrainee(Trainee trainee) {
        String password = profileService.generatePassword();
        trainee.setPassword(password);

        setUsername(trainee);

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
        log.warn("Trainee profile update triggered for ID: {}", trainee.getUserId());
        setUsername(trainee);

        return traineeDao.update(trainee);
    }

    @Override
    public void deleteTrainee(Long id) {
        traineeDao.delete(id);
    }

    private void setUsername(Trainee trainee){
        String username = profileService.createUsername(
                trainee.getFirstName(),
                trainee.getLastName()
        );

        trainee.setUsername(username);
    }
}
