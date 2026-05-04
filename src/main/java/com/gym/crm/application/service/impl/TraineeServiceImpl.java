package com.gym.crm.application.service.impl;

import com.gym.crm.application.dao.TraineeDaoHibernate;
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

    private final TraineeDaoHibernate traineeDao;
    private final ProfileService profileService;
    private final TraineeValidator traineeValidator;

    @Override
    public Trainee createTrainee(Trainee trainee) {
        traineeValidator.validateForCreate(trainee);

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

        Trainee created = traineeDao.create(traineeToCreate);

        log.info("Trainee profile created with username: {}", username);
        return created;
    }

    @Override
    public Trainee getTraineeById(Long id) {
        return traineeDao.findById(id).orElseThrow(() ->
                new NoSuchElementException("Trainee with ID " + id + " not found"));
    }

    @Override
    public Trainee getTraineeByUsername(String username) {
        traineeValidator.validateUsername(username);

        return traineeDao.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Trainee with username " + username + " not found"));
    }

    @Override
    public List<Trainee> getAllTrainees() {
        return traineeDao.findAll();
    }

    @Override
    public Trainee updateTrainee(Trainee trainee) {
        traineeValidator.validateForUpdate(trainee);

        Trainee existing = getTraineeById(trainee.getId());
        User existingUser = existing.getUser();
        User newUser = trainee.getUser();
        User userToUpdate = User.builder()
                .id(existingUser.getId())
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .username(existingUser.getUsername())
                .password(existingUser.getPassword())
                .isActive(existingUser.isActive())
                .build();
        Trainee traineeToUpdate = Trainee.builder()
                .id(existing.getId())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .user(userToUpdate)
                .build();

        return traineeDao.update(traineeToUpdate);
    }

    @Override
    public boolean isPasswordCorrect(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return false;
        }

        return traineeDao.findByUsername(username)
                .map(trainee -> trainee.getUser().getPassword().equals(password))
                .orElse(false);
    }

    @Override
    public void changePassword(String username, String newPassword) {
        traineeValidator.validateUsername(username);
        traineeValidator.validateNewPassword(newPassword);

        Trainee existing = getTraineeByUsername(username);
        User existingUser = existing.getUser();

        User userToUpdate = User.builder()
                .id(existingUser.getId())
                .firstName(existingUser.getFirstName())
                .lastName(existingUser.getLastName())
                .username(existingUser.getUsername())
                .password(newPassword)
                .isActive(existingUser.isActive())
                .build();

        Trainee traineeToUpdate = Trainee.builder()
                .id(existing.getId())
                .dateOfBirth(existing.getDateOfBirth())
                .address(existing.getAddress())
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
        traineeValidator.validateUsername(username);

        traineeDao.deleteByUsername(username);
        log.info("Trainee profile deleted by username: {}", username);
    }

    @Override
    public List<Training> getTraineeTrainings(String username, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingTypeName) {
        traineeValidator.validateUsername(username);

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
        traineeValidator.validateUsername(traineeUsername);

        return traineeDao.findNotAssignedTrainers(traineeUsername);
    }

    @Override
    public Trainee updateTrainersList(String traineeUsername, Set<Trainer> trainers) {
        traineeValidator.validateUsername(traineeUsername);
        traineeValidator.validateTrainersList(trainers);

        Trainee updated = traineeDao.updateTrainersList(traineeUsername, trainers);

        log.info("Trainers list updated for trainee username: {}", traineeUsername);
        return updated;
    }

    private Trainee updateActiveStatus(Trainee trainee, boolean active) {
        User existingUser = trainee.getUser();

        User userToUpdate = User.builder()
                .id(existingUser.getId())
                .firstName(existingUser.getFirstName())
                .lastName(existingUser.getLastName())
                .username(existingUser.getUsername())
                .password(existingUser.getPassword())
                .isActive(active)
                .build();

        Trainee traineeToUpdate = Trainee.builder()
                .id(trainee.getId())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .user(userToUpdate)
                .build();

        Trainee updated = traineeDao.update(traineeToUpdate);

        log.info("Trainee active status changed to {} for username: {}", active, existingUser.getUsername());

        return updated;
    }
}