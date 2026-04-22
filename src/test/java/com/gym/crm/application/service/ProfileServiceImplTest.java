package com.gym.crm.application.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gym.crm.application.dao.TraineeDao;
import com.gym.crm.application.dao.TrainerDao;
import com.gym.crm.application.model.Trainee;
import com.gym.crm.application.model.Trainer;
import com.gym.crm.application.service.impl.ProfileServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProfileServiceImplTest {

    private final String USER_FIRST_NAME = "Ivan";
    private final String USER_LAST_NAME = "Ivanov";
    private final String USERNAME = USER_FIRST_NAME + '.' + USER_LAST_NAME;
    private final String USERNAME_PLUS_ONE = USERNAME + "1";

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private TrainerDao trainerDao;

    @InjectMocks
    private ProfileServiceImpl profileService;

    private ListAppender<ILoggingEvent> listAppender;

    private Logger logger = (Logger) LoggerFactory.getLogger(ProfileServiceImpl.class);

    @BeforeEach
    void setUp() {
        when(traineeDao.findAll()).thenReturn(Collections.emptyList());
        when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        lenient().when(traineeDao.findAll()).thenReturn(Collections.emptyList());
        lenient().when(trainerDao.findAll()).thenReturn(Collections.emptyList());

        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    @DisplayName("Should successfully generate a standard username by joining first and last name with a dot")
    void createUsername_SimpleCase() {
        String actual = profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME);
        assertEquals(USERNAME, actual);
    }

    @Test
    @DisplayName("Should append an index to the username when a collision with an existing user occurs")
    void createUsername_WithCollision() {
        Trainee existingTrainee = Trainee.builder().username(USERNAME).build();

        when(traineeDao.findAll()).thenReturn(List.of(existingTrainee));

        String actual = profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME);

        assertEquals(USERNAME_PLUS_ONE, actual);
    }

    @Test
    @DisplayName("Should increment username suffix correctly when multiple collisions exist across both Trainee and Trainer records")
    void createUsername_MultipleCollisions() {
        Trainee t1 = Trainee.builder().username(USERNAME).build();
        Trainer tr1 = Trainer.builder().username(USERNAME_PLUS_ONE).build();

        when(traineeDao.findAll()).thenReturn(List.of(t1));
        when(trainerDao.findAll()).thenReturn(List.of(tr1));

        String actual = profileService.createUsername(USER_FIRST_NAME, USER_LAST_NAME);

        assertEquals(USERNAME + 2, actual);
    }

    @Test
    @DisplayName("Should generate a secure, random 10-character password on each call")
    void generatePassword_Test() {
        String pass1 = profileService.generatePassword();
        String pass2 = profileService.generatePassword();

        assertNotNull(pass1);
        assertEquals(10, pass1.length());
        assertNotEquals(pass1, pass2);
    }

    @Test
    @DisplayName("Should log INFO message when a duplicate username is detected during generation")
    void createUsername_ShouldLogWhenDuplicateFound() {
        String firstName = "Ivan";
        String lastName = "Ivanov";
        Trainee existingTrainee = new Trainee();
        existingTrainee.setUsername("Ivan.Ivanov");

        when(traineeDao.findAll()).thenReturn(List.of(existingTrainee));

        profileService.createUsername(firstName, lastName);

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .contains(tuple("Username 'Ivan.Ivanov' already exists. Starting serial number generation for Ivan Ivanov", Level.INFO));
    }
}