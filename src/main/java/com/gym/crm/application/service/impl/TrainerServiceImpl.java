package com.gym.crm.application.service.impl;

import com.gym.crm.application.dao.TrainerDaoHibernate;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.Training;
import com.gym.crm.application.entity.User;
import com.gym.crm.application.search.filter.TrainerTrainingSearchFilter;
import com.gym.crm.application.service.ProfileService;
import com.gym.crm.application.service.TrainerService;
import com.gym.crm.application.validation.TrainerValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TrainerServiceImpl implements TrainerService {

    private final TrainerDaoHibernate trainerDao;
    private final ProfileService profileService;
    private final TrainerValidator trainerValidator;

    @Override
    public Trainer createTrainer(Trainer trainer) {
        trainerValidator.validateForCreate(trainer);

        User user = trainer.getUser();
        String username = profileService.createUsername(user.getFirstName(), user.getLastName());
        String password = profileService.generatePassword();
        User userWithCredentials = User.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(username)
                .password(password)
                .isActive(true)
                .build();
        Trainer trainerToCreate = Trainer.builder()
                .user(userWithCredentials)
                .specialization(trainer.getSpecialization())
                .build();
        Trainer created = trainerDao.create(trainerToCreate);

        log.info("Trainer profile created with username: {}", username);
        return created;
    }

    @Override
    public Trainer getTrainerById(Long id) {
        return trainerDao.findById(id).orElseThrow(() -> new NoSuchElementException("Trainer with ID " + id + " not found"));
    }

    @Override
    public Trainer getTrainerByUsername(String username) {
        trainerValidator.validateUsername(username);

        return trainerDao.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException("Trainer with username " + username + " not found"));
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return trainerDao.findAll();
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        trainerValidator.validateForUpdate(trainer);

        Trainer existing = getTrainerById(trainer.getId());
        User existingUser = existing.getUser();
        User newUser = trainer.getUser();
        User userToUpdate = User.builder()
                .id(existingUser.getId())
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .username(existingUser.getUsername())
                .password(existingUser.getPassword())
                .isActive(existingUser.isActive())
                .build();
        Trainer trainerToUpdate = Trainer.builder()
                .id(existing.getId())
                .user(userToUpdate)
                .specialization(trainer.getSpecialization())
                .build();
        Trainer updated = trainerDao.update(trainerToUpdate);

        log.info("Trainer profile updated for username: {}", trainer.getUser().getUsername());
        return updated;
    }

    @Override
    public boolean isPasswordCorrect(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return false;
        }

        return trainerDao.findByUsername(username)
                .map(trainer -> trainer.getUser().getPassword().equals(password))
                .orElse(false);
    }

    @Override
    public void changePassword(String username, String newPassword) {
        trainerValidator.validateNewPassword(newPassword);

        Trainer existing = getTrainerByUsername(username);
        User existingUser = existing.getUser();
        User userToUpdate = User.builder()
                .id(existingUser.getId())
                .firstName(existingUser.getFirstName())
                .lastName(existingUser.getLastName())
                .username(existingUser.getUsername())
                .password(newPassword)
                .isActive(existingUser.isActive())
                .build();
        Trainer trainerToUpdate = Trainer.builder()
                .id(existing.getId())
                .user(userToUpdate)
                .specialization(existing.getSpecialization())
                .build();

        trainerDao.update(trainerToUpdate);
        log.info("Password changed for trainer username: {}", username);
    }

    @Override
    public Trainer activateTrainer(String username) {
        Trainer trainer = getTrainerByUsername(username);

        if (trainer.getUser().isActive()) {
            throw new IllegalStateException("Trainer is already active");
        }

        return updateActiveStatus(trainer, true);
    }

    @Override
    public Trainer deactivateTrainer(String username) {
        Trainer trainer = getTrainerByUsername(username);

        if (!trainer.getUser().isActive()) {
            throw new IllegalStateException("Trainer is already inactive");
        }

        return updateActiveStatus(trainer, false);
    }

    @Override
    public List<Training> getTrainerTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName) {
        trainerValidator.validateUsername(username);

        TrainerTrainingSearchFilter filter = TrainerTrainingSearchFilter.builder()
                .username(username)
                .fromDate(fromDate)
                .toDate(toDate)
                .traineeName(traineeName)
                .build();

        return trainerDao.findTrainingsByCriteria(filter);
    }

    private Trainer updateActiveStatus(Trainer trainer, boolean active) {
        User existingUser = trainer.getUser();
        User userToUpdate = User.builder()
                .id(existingUser.getId())
                .firstName(existingUser.getFirstName())
                .lastName(existingUser.getLastName())
                .username(existingUser.getUsername())
                .password(existingUser.getPassword())
                .isActive(active)
                .build();
        Trainer trainerToUpdate = Trainer.builder()
                .id(trainer.getId())
                .user(userToUpdate)
                .specialization(trainer.getSpecialization())
                .build();

        Trainer updated = trainerDao.update(trainerToUpdate);

        log.info("Trainer active status changed to {} for username: {}", active, existingUser.getUsername());
        return updated;
    }
}