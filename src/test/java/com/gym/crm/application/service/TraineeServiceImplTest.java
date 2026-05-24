package com.gym.crm.application.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gym.crm.application.dao.TraineeDao;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.Training;
import com.gym.crm.application.entity.User;
import com.gym.crm.application.search.filter.TraineeTrainingSearchFilter;
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
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TraineeServiceImplTest {

    private final Long TRAINEE_ID = 1L;
    private final Long USER_ID = 10L;
    private final String FIRST_NAME = "Ivan";
    private final String LAST_NAME = "Ivanov";
    private final String USERNAME = FIRST_NAME + "." + LAST_NAME;
    private final String PASSWORD = "randomPass123";

    @Mock
    private TraineeDao traineeDao;

    @Mock
    private ProfileService profileService;

    @Mock
    private TraineeValidator traineeValidator;

    @Mock
    private PasswordEncoder passwordEncoder;

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
    @DisplayName("Should create trainee with generated username and encoded password")
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

        String rawPassword = "raw_password_123";
        String encodedHash = "hashed_content";

        when(profileService.createUsername(FIRST_NAME, LAST_NAME)).thenReturn(USERNAME);
        when(profileService.generatePassword()).thenReturn(rawPassword);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedHash);

        when(traineeDao.create(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee actual = traineeService.createTrainee(trainee);

        assertNotNull(actual);
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertNotNull(actual.getUser());
        assertEquals(rawPassword, actual.getUser().getPassword());
        assertTrue(actual.getUser().isActive());
        verify(traineeValidator).validateForCreate(trainee);
        verify(profileService).createUsername(FIRST_NAME, LAST_NAME);
        verify(profileService).generatePassword();
        verify(passwordEncoder).encode(rawPassword);
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
    void getTraineeById_whenNotFound() {
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
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .build();
        Trainee updateRequest = Trainee.builder()
                .id(TRAINEE_ID)
                .user(user)
                .dateOfBirth(LocalDate.of(1999, 5, 10))
                .address("Lviv")
                .build();

        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.of(existing));
        when(traineeDao.update(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee actual = traineeService.updateTrainee(updateRequest);

        assertEquals(TRAINEE_ID, actual.getId());
        assertEquals(USER_ID, actual.getUser().getId());
        assertEquals(FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(LAST_NAME, actual.getUser().getLastName());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertEquals(USERNAME, actual.getUser().getUsername());
        assertEquals(PASSWORD, actual.getUser().getPassword());
        assertTrue(actual.getUser().isActive());
        assertEquals(LocalDate.of(1999, 5, 10), actual.getDateOfBirth());
        assertEquals("Lviv", actual.getAddress());
        verify(traineeValidator).validateForUpdate(updateRequest);
        verify(traineeDao).findByUsername(USERNAME);
        verify(traineeDao).update(any(Trainee.class));
    }

    @Test
    @DisplayName("Should pass correctly rebuilt trainee to DAO during update")
    void updateTrainee_shouldPassCorrectTraineeToDao() {
        Trainee existing = buildTrainee(true);
        User user = User.builder()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .build();
        Trainee updateRequest = Trainee.builder()
                .id(TRAINEE_ID)
                .user(user)
                .dateOfBirth(LocalDate.of(1999, 5, 10))
                .address("Lviv")
                .build();

        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.of(existing));
        when(traineeDao.update(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        traineeService.updateTrainee(updateRequest);

        ArgumentCaptor<Trainee> captor = ArgumentCaptor.forClass(Trainee.class);
        verify(traineeDao).update(captor.capture());

        Trainee passedToDao = captor.getValue();

        assertEquals(TRAINEE_ID, passedToDao.getId());
        assertEquals(USER_ID, passedToDao.getUser().getId());
        assertEquals(FIRST_NAME, passedToDao.getUser().getFirstName());
        assertEquals(LAST_NAME, passedToDao.getUser().getLastName());
        assertEquals(USERNAME, passedToDao.getUser().getUsername());
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

    @Test
    @DisplayName("Should delete trainee by username")
    void deleteTraineeByUsername_shouldValidateUsernameAndDelete() {
        String username = "test.user";

        traineeService.deleteTraineeByUsername(username);

        verify(traineeValidator).validateUsername(username);
        verify(traineeDao).deleteByUsername(username);
    }

    @Test
    @DisplayName("Should get trainee trainings by criteria")
    void getTraineeTrainings_shouldBuildFilterAndReturnTrainings() {
        String username = "test.user";
        LocalDate fromDate = LocalDate.of(2026, 1, 1);
        LocalDate toDate = LocalDate.of(2026, 1, 31);
        String trainerName = "Ivan";
        String trainingTypeName = "Yoga";
        Training training = Training.builder()
                .id(1L)
                .trainingName("Morning Yoga")
                .build();

        when(traineeDao.findTrainingsByCriteria(any(TraineeTrainingSearchFilter.class)))
                .thenReturn(List.of(training));

        List<Training> actual = traineeService.getTraineeTrainings(username, fromDate, toDate, trainerName, trainingTypeName);

        assertEquals(1, actual.size());
        assertEquals(training, actual.get(0));

        verify(traineeValidator).validateUsername(username);
        verify(traineeDao).findTrainingsByCriteria(argThat(filter ->
                filter.getUsername().equals(username)
                        && filter.getFromDate().equals(fromDate)
                        && filter.getToDate().equals(toDate)
                        && filter.getTrainerName().equals(trainerName)
                        && filter.getTrainingTypeName().equals(trainingTypeName)));
    }

    @Test
    @DisplayName("Should get not assigned trainers")
    void getNotAssignedTrainers_shouldValidateUsernameAndReturnTrainers() {
        String username = "test.user";
        Trainer trainer = Trainer.builder()
                .id(1L)
                .build();

        when(traineeDao.findNotAssignedTrainers(username)).thenReturn(List.of(trainer));

        List<Trainer> actual = traineeService.getNotAssignedTrainers(username);

        assertEquals(1, actual.size());
        assertEquals(trainer, actual.get(0));
        verify(traineeValidator).validateUsername(username);
        verify(traineeDao).findNotAssignedTrainers(username);
    }

    @Test
    @DisplayName("Should update trainee trainers list")
    void updateTrainersList_shouldValidateAndUpdateTrainersList() {
        String username = "test.user";
        Trainer trainer = Trainer.builder()
                .id(1L)
                .build();
        Set<Trainer> trainers = Set.of(trainer);
        Trainee updatedTrainee = Trainee.builder()
                .id(1L)
                .trainers(trainers)
                .build();

        when(traineeDao.updateTrainersList(username, trainers)).thenReturn(updatedTrainee);

        Trainee actual = traineeService.updateTrainersList(username, trainers);

        assertEquals(updatedTrainee, actual);
        verify(traineeValidator).validateUsername(username);
        verify(traineeValidator).validateTrainersList(trainers);
        verify(traineeDao).updateTrainersList(username, trainers);
    }

    @Test
    @DisplayName("Should activate inactive trainee")
    void changeActiveStatus_shouldActivateInactiveTrainee() {
        User user = User.builder()
                .username(USERNAME)
                .isActive(false)
                .build();
        Trainee trainee = Trainee.builder()
                .id(TRAINEE_ID)
                .user(user)
                .build();

        when(traineeDao.findByUsername(USERNAME)).thenReturn(Optional.of(trainee));
        when(traineeDao.update(any(Trainee.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Trainee actual = traineeService.changeActiveStatus(USERNAME, true);

        assertTrue(actual.getUser().isActive());

        verify(traineeDao).findByUsername(USERNAME);
        verify(traineeDao).update(argThat(updated -> updated.getUser().isActive() && updated.getUser().getUsername().equals(USERNAME)));
    }
}