package com.gym.crm.application.service;

import com.gym.crm.application.dao.TraineeDao;
import com.gym.crm.application.model.Trainee;
import com.gym.crm.application.service.impl.TraineeServiceImpl;
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
public class TraineeServiceImplTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private ProfileService profileService;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private final String USER_FIRST_NAME = "Ivan";
    private final String USER_LAST_NAME = "Ivanov";
    private final String USERNAME = USER_FIRST_NAME + '.' + USER_LAST_NAME;
    private final Long ENTITY_ID = 1L;

    @Test
    void createTrainee_ShouldWorkCorrectly() {
        Trainee trainee = Trainee.builder()
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .build();

        String expectedPassword = "randomPass123";

        when(profileService.generatePassword()).thenReturn(expectedPassword);
        when(profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME)).thenReturn(USERNAME);
        when(traineeDao.create(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee result = traineeService.createTrainee(trainee);

        assertNotNull(result);
        assertEquals(expectedPassword, result.getPassword());
        assertEquals(USERNAME, result.getUsername());
        verify(profileService).generatePassword();
        verify(profileService).createUsername(USER_FIRST_NAME, USER_LAST_NAME);
        verify(traineeDao).create(trainee);
    }

    @Test
    void getTraineeById_WhenFound() {
        Trainee trainee = Trainee.builder().id(ENTITY_ID).firstName(USER_FIRST_NAME).build();
        when(traineeDao.findById(ENTITY_ID)).thenReturn(Optional.of(trainee));

        Trainee result = traineeService.getTraineeById(ENTITY_ID);

        assertEquals(ENTITY_ID, result.getId());
        assertEquals(USER_FIRST_NAME, result.getFirstName());
    }

    @Test
    void getTraineeById_WhenNotFound() {
        Long id = 99L;
        when(traineeDao.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> traineeService.getTraineeById(id));
    }

    @Test
    void updateTrainee_ShouldUpdateUsername() {
        Trainee trainee = Trainee.builder()
                .id(ENTITY_ID)
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .build();

        when(profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME)).thenReturn(USERNAME);
        when(traineeDao.update(trainee)).thenReturn(trainee);

        Trainee result = traineeService.updateTrainee(trainee);

        assertEquals(USERNAME, result.getUsername());
        verify(traineeDao).update(trainee);
    }

    @Test
    void getAllTrainees_ShouldReturnList() {
        List<Trainee> trainees = List.of(new Trainee(), new Trainee());
        when(traineeDao.findAll()).thenReturn(trainees);

        List<Trainee> result = traineeService.getAllTrainees();

        assertEquals(2, result.size());
        verify(traineeDao).findAll();
    }

    @Test
    void deleteTrainee_ShouldCallDao() {
        traineeService.deleteTrainee(ENTITY_ID);

        verify(traineeDao).delete(ENTITY_ID);
    }
}