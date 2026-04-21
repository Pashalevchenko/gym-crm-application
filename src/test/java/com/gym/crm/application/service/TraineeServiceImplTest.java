package com.gym.crm.application.service;

import com.gym.crm.application.dao.TraineeDao;
import com.gym.crm.application.model.Trainee;
import com.gym.crm.application.service.impl.TraineeServiceImpl;
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
public class TraineeServiceImplTest {

    private final String USER_FIRST_NAME = "Ivan";
    private final String USER_LAST_NAME = "Ivanov";
    private final String USERNAME = USER_FIRST_NAME + '.' + USER_LAST_NAME;
    private final Long ENTITY_ID = 1L;

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    @Test
    @DisplayName("Verify that service correctly populates trainee with generated username and password before saving")
    void createTrainee_ShouldWorkCorrectly() {
        Trainee trainee = Trainee.builder()
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .build();

        String expectedPassword = "randomPass123";

        when(profileService.generatePassword()).thenReturn(expectedPassword);
        when(profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME)).thenReturn(USERNAME);
        when(traineeDao.create(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee actual = traineeService.createTrainee(trainee);

        assertNotNull(actual);
        assertEquals(expectedPassword, actual.getPassword());
        assertEquals(USERNAME, actual.getUsername());
        verify(profileService).generatePassword();
        verify(profileService).createUsername(USER_FIRST_NAME, USER_LAST_NAME);
        verify(traineeDao).create(trainee);
    }

    @Test
    @DisplayName("Should return trainee when a valid ID is provided")
    void getTraineeById_WhenFound() {
        Trainee trainee = Trainee.builder().id(ENTITY_ID).firstName(USER_FIRST_NAME).build();

        when(traineeDao.findById(ENTITY_ID)).thenReturn(Optional.of(trainee));

        Trainee actual = traineeService.getTraineeById(ENTITY_ID);

        assertEquals(ENTITY_ID, actual.getId());
        assertEquals(USER_FIRST_NAME, actual.getFirstName());
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when trainee ID does not exist in the database")
    void getTraineeById_WhenNotFound() {
        Long id = 99L;

        when(traineeDao.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> traineeService.getTraineeById(id));
    }

    @Test
    @DisplayName("Should recalculate and update username when trainee profile data is modified")
    void updateTrainee_ShouldUpdateUsername() {
        Trainee trainee = Trainee.builder()
                .id(ENTITY_ID)
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .build();

        when(profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME)).thenReturn(USERNAME);
        when(traineeDao.update(trainee)).thenReturn(trainee);

        Trainee actual = traineeService.updateTrainee(trainee);

        assertEquals(USERNAME, actual.getUsername());
        verify(traineeDao).update(trainee);
    }

    @Test
    @DisplayName("Should successfully retrieve all trainees from the database as a list")
    void getAllTrainees_ShouldReturnList() {
        List<Trainee> trainees = List.of(new Trainee(), new Trainee());

        when(traineeDao.findAll()).thenReturn(trainees);

        List<Trainee> actual = traineeService.getAllTrainees();

        assertEquals(2, actual.size());
        verify(traineeDao).findAll();
    }

    @Test
    @DisplayName("Should successfully delegate the deletion of a trainee to the DAO layer using their ID")
    void deleteTrainee_ShouldCallDao() {
        traineeService.deleteTrainee(ENTITY_ID);

        verify(traineeDao).delete(ENTITY_ID);
    }
}