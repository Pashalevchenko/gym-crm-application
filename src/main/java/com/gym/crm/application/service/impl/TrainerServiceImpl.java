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
        User userWithCredentials = user.toBuilder()
                .username(username)
                .password(password)
                .isActive(true)
                .build();
        Trainer trainerToCreate = trainer.toBuilder()
                .user(userWithCredentials)
                .build();
        Trainer created = trainerDao.create(trainerToCreate);

        log.info("Trainer profile created with username: {}", username);
        return created;
    }

    @Override
    public Trainer getTrainerById(Long id) {
        return trainerDao.findById(id).orElseThrow(() -> new NoSuchElementException(String.format("Trainer with ID %d not found", id)));
    }

    @Override
    public Trainer getTrainerByUsername(String username) {
        trainerValidator.validateUsername(username);

        return trainerDao.findByUsername(username)
                .orElseThrow(() -> new NoSuchElementException(String.format("Trainer with username %s  not found", username)));
    }

    @Override
    public List<Trainer> getAllTrainers() {
        return trainerDao.findAll();
    }

    @Override
    public Trainer updateTrainer(Trainer trainer) {
        trainerValidator.validateForUpdate(trainer);

        Trainer existing = getTrainerById(trainer.getId());
        User userToUpdate = existing.getUser().toBuilder()
                .firstName(trainer.getUser().getFirstName())
                .lastName(trainer.getUser().getLastName())
                .build();
        Trainer trainerToUpdate = existing.toBuilder()
                .specialization(trainer.getSpecialization())
                .user(userToUpdate)
                .build();
        Trainer updated = trainerDao.update(trainerToUpdate);

        log.info("Trainer profile updated for username: {}", updated.getUser().getUsername());
        return updated;
    }

    @Override
    public void changePassword(String username, String newPassword) {
        trainerValidator.validateNewPassword(newPassword);

        Trainer existing = getTrainerByUsername(username);
        User userToUpdate = existing.getUser().toBuilder()
                .password(newPassword)
                .build();
        Trainer trainerToUpdate = existing.toBuilder()
                .user(userToUpdate)
                .build();

        trainerDao.update(trainerToUpdate);
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
        User userToUpdate = trainer.getUser().toBuilder()
                .isActive(active)
                .build();
        Trainer trainerToUpdate = trainer.toBuilder()
                .user(userToUpdate)
                .build();

        return trainerDao.update(trainerToUpdate);
    }
}