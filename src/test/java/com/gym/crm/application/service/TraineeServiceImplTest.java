package com.gym.crm.application.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gym.crm.application.dao.TraineeDaoHibernate;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.User;
import com.gym.crm.application.service.impl.TraineeServiceImpl;
import com.gym.crm.application.validation.TraineeValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    private static final Long TRAINEE_ID = 1L;
    private static final Long USER_ID = 10L;
    private static final String FIRST_NAME = "Ivan";
    private static final String LAST_NAME = "Ivanov";
    private static final String USERNAME = FIRST_NAME + "." + LAST_NAME;
    private static final String PASSWORD = "randomPass123";

    @Mock
    private TraineeDaoHibernate traineeDao;

    @Mock
    private ProfileService profileService;

    @Mock
    private TraineeValidator traineeValidator;

    @InjectMocks
    private TraineeServiceImpl traineeService;

    private ListAppender<ILoggingEvent> listAppender;
    private Logger logger;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(TraineeServiceImpl.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @AfterEach
    void tearDown() {
        logger.detachAppender(listAppender);
    }

    @Test
    @DisplayName("Should create trainee with generated username and password")
    void createTrainee_shouldCreateTraineeWithGeneratedCredentials() {
        User user = User.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .build();
        Trainee trainee = Trainee.builder()
                .user(user)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Kyiv")
                .build();

        when(profileService.createUsername(FIRST_NAME, LAST_NAME)).thenReturn(USERNAME);
        when(profileService.generatePassword()).thenReturn(PASSWORD);
        when(traineeDao.create(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee actual = traineeService.createTrainee(trainee);

        assertNotNull(actual);
        assertNotNull(actual.getUser());
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertEquals(PASSWORD, actual.getUser().getPassword());
        assertTrue(actual.getUser().isActive());
        assertEquals(LocalDate.of(2000, 1, 1), actual.getDateOfBirth());
        assertEquals("Kyiv", actual.getAddress());
        verify(traineeValidator).validateForCreate(trainee);
        verify(profileService).createUsername(FIRST_NAME, LAST_NAME);
        verify(profileService).generatePassword();
        verify(traineeDao).create(any(Trainee.class));
    }

    @Test
    @DisplayName("Should return trainee when valid ID is provided")
    void getTraineeById_whenFound_shouldReturnTrainee() {
        Trainee trainee = buildTrainee(true);

        when(traineeDao.findById(TRAINEE_ID)).thenReturn(Optional.of(trainee));

        Trainee actual = traineeService.getTraineeById(TRAINEE_ID);

        assertEquals(TRAINEE_ID, actual.getId());
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        verify(traineeDao).findById(TRAINEE_ID);
    }

    @Test
    @DisplayName("Should throw NoSuchElementException when trainee ID does not exist")
    void getTraineeById_whenNotFound_shouldThrowException() {
        Long id = 99L;

        when(traineeDao.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> traineeService.getTraineeById(id));

        verify(traineeDao).findById(id);
    }

    @Test
    @DisplayName("Should update trainee profile and preserve username, password and active status")
    void updateTrainee_shouldUpdateProfileAndPreserveCredentials() {
        Trainee existing = buildTrainee(true);
        User user = User.builder()
                .firstName("Petro")
                .lastName("Petrenko")
                .build();
        Trainee updateRequest = Trainee.builder()
                .id(TRAINEE_ID)
                .user(user)
                .dateOfBirth(LocalDate.of(1999, 5, 10))
                .address("Lviv")
                .build();

        when(traineeDao.findById(TRAINEE_ID)).thenReturn(Optional.of(existing));
        when(traineeDao.update(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee actual = traineeService.updateTrainee(updateRequest);

        assertEquals(TRAINEE_ID, actual.getId());
        assertEquals(USER_ID, actual.getUser().getId());
        assertEquals("Petro", actual.getUser().getFirstName());
        assertEquals("Petrenko", actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertEquals(PASSWORD, actual.getUser().getPassword());
        assertTrue(actual.getUser().isActive());
        assertEquals(LocalDate.of(1999, 5, 10), actual.getDateOfBirth());
        assertEquals("Lviv", actual.getAddress());
        verify(traineeValidator).validateForUpdate(updateRequest);
        verify(traineeDao).findById(TRAINEE_ID);
        verify(traineeDao).update(any(Trainee.class));
    }

    @Test
    @DisplayName("Should pass correctly rebuilt trainee to DAO during update")
    void updateTrainee_shouldPassCorrectTraineeToDao() {
        Trainee existing = buildTrainee(true);
        User user = User.builder()
                .firstName("Petro")
                .lastName("Petrenko")
                .build();
        Trainee updateRequest = Trainee.builder()
                .id(TRAINEE_ID)
                .user(user)
                .dateOfBirth(LocalDate.of(1999, 5, 10))
                .address("Lviv")
                .build();

        when(traineeDao.findById(TRAINEE_ID)).thenReturn(Optional.of(existing));
        when(traineeDao.update(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        traineeService.updateTrainee(updateRequest);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeDao).update(captor.capture());

        Trainee passedToDao = captor.getValue();

        assertEquals(TRAINEE_ID, passedToDao.getId());
        assertEquals(USER_ID, passedToDao.getUser().getId());
        assertEquals("Petro", passedToDao.getUser().getFirstName());
        assertEquals("Petrenko", passedToDao.getUser().getLastName());
        assertEquals(USERNAME, passedToDao.getUser().getUsername());
        assertEquals(PASSWORD, passedToDao.getUser().getPassword());
        assertTrue(passedToDao.getUser().isActive());
        assertEquals(LocalDate.of(1999, 5, 10), passedToDao.getDateOfBirth());
        assertEquals("Lviv", passedToDao.getAddress());
    }

    @Test
    @DisplayName("Should return all trainees")
    void getAllTrainees_shouldReturnList() {
        User user = User.builder()
                .id(20L)
                .firstName("Anna")
                .lastName("Smith")
                .username("anna.smith")
                .password("pass")
                .isActive(true)
                .build();
        List<Trainee> trainees = List.of(buildTrainee(true),
                Trainee.builder()
                        .id(2L)
                        .user(user)
                        .build()
        );

        when(traineeDao.findAll()).thenReturn(trainees);

        List<Trainee> actual = traineeService.getAllTrainees();

        assertEquals(2, actual.size());
        assertEquals(USERNAME, actual.get(0).getUser().getUsername());
        assertEquals("anna.smith", actual.get(1).getUser().getUsername());
        verify(traineeDao).findAll();
    }

    @Test
    @DisplayName("Should delegate trainee deletion to DAO by ID")
    void deleteTrainee_shouldCallDao() {
        traineeService.deleteTrainee(TRAINEE_ID);

        verify(traineeDao).delete(TRAINEE_ID);

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .contains(tuple("Trainee profile deleted with id: " + TRAINEE_ID, Level.INFO));
    }

    private Trainee buildTrainee(boolean active) {
        User user = User.builder()
                .id(USER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .password(PASSWORD)
                .isActive(active)
                .build();

        return Trainee.builder()
                .id(TRAINEE_ID)
                .user(user)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Kyiv")
                .build();
    }
}