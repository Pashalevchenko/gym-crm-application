package com.gym.crm.application.service;

import com.gym.crm.application.dao.TraineeDao;
import com.gym.crm.application.dao.TrainerDao;
import com.gym.crm.application.model.Trainee;
import com.gym.crm.application.model.Trainer;
import com.gym.crm.application.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceImplTest {

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private final String USER_FIRST_NAME = "Ivan";
    private final String USER_LAST_NAME = "Ivanov";
    private final String USERNAME = USER_FIRST_NAME + '.' + USER_LAST_NAME;
    private final String USERNAME_PLUS_ONE = USERNAME + "1";

    @BeforeEach
    void setUp() {
        when(traineeDao.findAll()).thenReturn(Collections.emptyList());
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        lenient().when(traineeDao.findAll()).thenReturn(Collections.emptyList());
        lenient().when(trainerDao.findAll()).thenReturn(Collections.emptyList());
    }

    @Test
    void createUsername_SimpleCase() {
        String result = profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME);
        assertEquals(USERNAME, result);
    }

    @Test
    void createUsername_WithCollision() {
        Trainee existingTrainee = Trainee.builder().username(USERNAME).build();
        when(traineeDao.findAll()).thenReturn(List.of(existingTrainee));

        String result = profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME);

        assertEquals(USERNAME_PLUS_ONE, result);
    }

    @Test
    void createUsername_MultipleCollisions() {
        Trainee t1 = Trainee.builder().username(USERNAME).build();
        Trainer tr1 = Trainer.builder().username(USERNAME_PLUS_ONE).build();

        when(traineeDao.findAll()).thenReturn(List.of(t1));
        when(trainerDao.findAll()).thenReturn(List.of(tr1));

        String result = profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME);

        assertEquals(USERNAME + 2, result);
    }

    @Test
    void generatePassword_Test() {
        String pass1 = profileService.generatePassword();
        String pass2 = profileService.generatePassword();

        assertNotNull(pass1);
        assertEquals(10, pass1.length());
        assertNotEquals(pass1, pass2);
    }
}