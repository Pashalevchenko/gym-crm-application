package com.gym.crm.application.service;

import com.gym.crm.application.dao.TrainerDao;
import com.gym.crm.application.model.Trainer;
import com.gym.crm.application.model.TrainingType;
import com.gym.crm.application.service.impl.TrainerServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceImplTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    private final String USER_FIRST_NAME = "Ivan";
    private final String USER_LAST_NAME = "Ivanov";
    private final String USERNAME = USER_FIRST_NAME + '.' + USER_LAST_NAME;
    private final String USERNAME_PLUS_ONE = USERNAME + "1";
    private final Long ENTITY_ID = 1L;

    @Test
    void createTrainer_ShouldWorkCorrectly() {
        Trainer trainer = Trainer.builder()
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .specialization(new TrainingType())
                .build();

        String generatedPass = "secure123";

        when(profileService.generatePassword()).thenReturn(generatedPass);
        when(profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME)).thenReturn(USERNAME);
        when(trainerDao.create(any(Trainer.class))).thenAnswer(i -> i.getArgument(0));

        Trainer result = trainerService.createTrainer(trainer);

        assertNotNull(result);
        assertEquals(generatedPass, result.getPassword());
        assertEquals(USERNAME, result.getUsername());
        verify(profileService).generatePassword();
        verify(profileService).createUsername(USER_FIRST_NAME, USER_LAST_NAME);
        verify(trainerDao).create(trainer);
    }

    @Test
    void getTrainerById_WhenFound() {
        Trainer trainer = Trainer.builder().id(ENTITY_ID).firstName(USER_FIRST_NAME).build();
        when(trainerDao.findById(ENTITY_ID)).thenReturn(Optional.of(trainer));

        Trainer result = trainerService.getTrainerById(ENTITY_ID);

        assertEquals(ENTITY_ID, result.getId());
        assertEquals(USER_FIRST_NAME, result.getFirstName());
    }

    @Test
    void getTrainerById_WhenNotFound() {
        Long id = 999L;
        when(trainerDao.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainerService.getTrainerById(id));
    }

    @Test
    @DisplayName("Should update username and call DAO")
    void updateTrainer_ShouldUpdateUsername() {
        Trainer trainer = Trainer.builder()
                .id(ENTITY_ID)
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .build();

        when(profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME)).thenReturn(USERNAME_PLUS_ONE);
        when(trainerDao.update(any(Trainer.class))).thenAnswer(i -> i.getArgument(0));

        Trainer result = trainerService.updateTrainer(trainer);

        assertEquals(USERNAME_PLUS_ONE, result.getUsername());
        verify(trainerDao).update(trainer);
    }

    @Test
    void getAllTrainers_ShouldReturnList() {
        List<Trainer> trainers = List.of(new Trainer(), new Trainer());

        when(trainerDao.findAll()).thenReturn(trainers);

        List<Trainer> result = trainerService.getAllTrainers();

        assertEquals(2, result.size());
        verify(trainerDao).findAll();
    }
}