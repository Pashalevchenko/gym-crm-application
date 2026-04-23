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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainerServiceImplTest {

    private final String USER_FIRST_NAME = "Ivan";
    private final String USER_LAST_NAME = "Ivanov";
    private final String USERNAME = USER_FIRST_NAME + '.' + USER_LAST_NAME;
    private final String USERNAME_PLUS_ONE = USERNAME + "1";
    private final Long ENTITY_ID = 1L;

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private TrainerServiceImpl trainerService;

    @Test
    @DisplayName("Should successfully create a trainer profile with generated credentials and specialization")
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

        Trainer actual = trainerService.createTrainer(trainer);

        assertNotNull(actual);
        assertEquals(generatedPass, actual.getPassword());
        assertEquals(USERNAME, actual.getUsername());
        verify(profileService).generatePassword();
        verify(profileService).createUsername(USER_FIRST_NAME, USER_LAST_NAME);
        verify(trainerDao).create(trainer);
    }

    @Test
    @DisplayName("Should return trainer profile when a valid ID is provided")
    void getTrainerById_WhenFound() {
        Trainer trainer = Trainer.builder().userId(ENTITY_ID).firstName(USER_FIRST_NAME).build();

        when(trainerDao.findById(ENTITY_ID)).thenReturn(Optional.of(trainer));

        Trainer actual = trainerService.getTrainerById(ENTITY_ID);

        assertEquals(ENTITY_ID, actual.getUserId());
        assertEquals(USER_FIRST_NAME, actual.getFirstName());
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when trainer ID does not exist in the system")
    void getTrainerById_WhenNotFound() {
        Long id = 999L;

        when(trainerDao.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> trainerService.getTrainerById(id));
    }

    @Test
    @DisplayName("Should update username and call DAO")
    void updateTrainer_ShouldUpdateUsername() {
        Trainer trainer = Trainer.builder()
                .userId(ENTITY_ID)
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .build();

        when(profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME)).thenReturn(USERNAME_PLUS_ONE);
        when(trainerDao.update(any(Trainer.class))).thenAnswer(i -> i.getArgument(0));

        Trainer actual = trainerService.updateTrainer(trainer);

        assertEquals(USERNAME_PLUS_ONE, actual.getUsername());
        verify(trainerDao).update(trainer);
    }

    @Test
    @DisplayName("Should successfully retrieve a complete list of all trainers from the database")
    void getAllTrainers_ShouldReturnList() {
        List<Trainer> trainers = List.of(new Trainer(), new Trainer());

        when(trainerDao.findAll()).thenReturn(trainers);

        List<Trainer> actual = trainerService.getAllTrainers();

        assertEquals(2, actual.size());
        verify(trainerDao).findAll();
    }
}