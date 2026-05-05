package com.gym.crm.application.service.impl;

import com.gym.crm.application.dao.TraineeDao;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.Training;
import com.gym.crm.application.entity.User;
import com.gym.crm.application.search.filter.TraineeTrainingSearchFilter;
import com.gym.crm.application.service.ProfileService;
import com.gym.crm.application.service.TraineeService;
import com.gym.crm.application.validation.TraineeValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TraineeServiceImpl implements TraineeService {

    private final TraineeDao traineeDao;
    private final ProfileService profileService;
    private final TraineeValidator validator;

    @Override
    public Trainee createTrainee(Trainee trainee) {
        validator.validateForCreate(trainee);

        User user = trainee.getUser();
        String username = profileService.createUsername(user.getFirstName(), user.getLastName());
        String password = profileService.generatePassword();

        User userWithCredentials = User.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(username)
                .password(password)
                .isActive(true)
                .build();
        Trainee traineeToCreate = Trainee.builder()
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .user(userWithCredentials)
                .build();

        return traineeDao.create(traineeToCreate);
    }

    @Override
    public Trainee getTraineeById(Long id) {
        return traineeDao.findById(id).orElseThrow(() ->
                new NoSuchElementException(String.format("Trainee with ID %d not found", id)));
    }

    @Override
    public Trainee getTraineeByUsername(String username) {
        validator.validateUsername(username);

        return traineeDao.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException(String.format("Trainee with username %s not found", username)));
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return traineeDao.findAll();
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        validator.validateForUpdate(trainee);

        Trainee existing = getTraineeById(trainee.getId());
        User userToUpdate = existing.getUser().toBuilder()
                .firstName(trainee.getUser().getFirstName())
                .lastName(trainee.getUser().getLastName())
                .build();
        Trainee traineeToUpdate = existing.toBuilder()
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .user(userToUpdate)
                .build();

        return traineeDao.update(traineeToUpdate);
    }

    @Override
    public void changePassword(String username, String newPassword) {
        validator.validateUsername(username);
        validator.validateNewPassword(newPassword);

        Trainee existing = getTraineeByUsername(username);
        User userToUpdate = existing.getUser().toBuilder()
                .password(newPassword)
                .build();
        Trainee traineeToUpdate = existing.toBuilder()
                .user(userToUpdate)
                .build();

        traineeDao.update(traineeToUpdate);

        log.info("Password changed for trainee username: {}", username);
    }

    @Override
    public Trainee activateTrainee(String username) {
        Trainee trainee = getTraineeByUsername(username);

        if (trainee.getUser().isActive()) {
            throw new IllegalStateException("Trainee is already active");
        }

        return updateActiveStatus(trainee, true);
    }

    @Override
    public Trainee deactivateTrainee(String username) {
        Trainee trainee = getTraineeByUsername(username);

        if (!trainee.getUser().isActive()) {
            throw new IllegalStateException("Trainee is already inactive");
        }

        return updateActiveStatus(trainee, false);
    }

    @Override
    public void deleteTrainee(Long id) {
        traineeDao.delete(id);

        log.info("Trainee profile deleted with id: {}", id);
    }

    @Override
    public void deleteTraineeByUsername(String username) {
        validator.validateUsername(username);

        traineeDao.deleteByUsername(username);
    }

    @Override
    public List<Training> getTraineeTrainings(String username, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingTypeName) {
        validator.validateUsername(username);

        TraineeTrainingSearchFilter filter = TraineeTrainingSearchFilter.builder()
                .username(username)
                .fromDate(fromDate)
                .toDate(toDate)
                .trainerName(trainerName)
                .trainingTypeName(trainingTypeName)
                .build();

        return traineeDao.findTrainingsByCriteria(filter);
    }

    @Override
    public List<Trainer> getNotAssignedTrainers(String traineeUsername) {
        validator.validateUsername(traineeUsername);

        return traineeDao.findNotAssignedTrainers(traineeUsername);
    }

    @Override
    public Trainee updateTrainersList(String traineeUsername, Set<Trainer> trainers) {
        validator.validateUsername(traineeUsername);
        validator.validateTrainersList(trainers);

        return traineeDao.updateTrainersList(traineeUsername, trainers);
    }

    private Trainee updateActiveStatus(Trainee trainee, boolean active) {
        User updatedUser = trainee.getUser().toBuilder()
                .isActive(active)
                .build();
        Trainee traineeToUpdate = trainee.toBuilder()
                .user(updatedUser)
                .build();

        return traineeDao.update(traineeToUpdate);
    }
}