package com.gym.crm.application.dao;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gym.crm.application.dao.impl.TraineeDaoImpl;
import com.gym.crm.application.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TraineeDaoImplTest {

    private final Long TRAINEE_ID = 1L;
    private final Long UNEXIST_TRAINEE_ID = 99L;

    @Mock
    private Map<Long, Trainee> storage;

    private TraineeDao traineeDao;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        traineeDao = new TraineeDaoImpl(storage);

        Logger logger = (Logger) LoggerFactory.getLogger(TraineeDaoImpl.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    @DisplayName("Should successfully store trainee in storage and return the same object")
    void create_ShouldStoreTrainee() {
        Trainee expected = Trainee.builder().id(TRAINEE_ID).firstName("Ivan").build();
        Trainee actual = traineeDao.create(expected);

        verify(storage).put(TRAINEE_ID, actual);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should successfully update trainee data when the ID exists in storage")
    void update_ShouldUpdateTrainee_WhenIdExists() {
        Trainee expected = Trainee.builder().id(TRAINEE_ID).firstName("Updated Name").build();
        when(storage.containsKey(TRAINEE_ID)).thenReturn(true);

        Trainee actual = traineeDao.update(expected);

        verify(storage).put(TRAINEE_ID, expected);
        assertEquals(expected, actual);

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getLevel)
                .doesNotContain(Level.ERROR);

        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .contains(tuple("Trainee with id: " + TRAINEE_ID + " was update", Level.INFO));
    }

    @Test
    @DisplayName("Should throw Exception and log ERROR when Trainee ID not found in storage")
    void update_ShouldThrowException_WhenIdDoesNotExist() {
        Trainee trainee = Trainee.builder().id(UNEXIST_TRAINEE_ID).build();

        when(storage.containsKey(UNEXIST_TRAINEE_ID)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                traineeDao.update(trainee)
        );

        assertTrue(exception.getMessage().contains("not found in storage"));
        verify(storage, never()).put(anyLong(), any());
        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .contains(tuple(
                        "Cannot update Trainee: ID " + UNEXIST_TRAINEE_ID + " not found in storage",
                        Level.ERROR
                ));
    }

    @Test
    @DisplayName("Should return an Optional containing the trainee when a valid ID is provided")
    void findById_ShouldReturnTrainee_WhenIdExists() {
        Trainee expected = Trainee.builder().id(TRAINEE_ID).build();

        when(storage.get(TRAINEE_ID)).thenReturn(expected);

        Optional<Trainee> actual = traineeDao.findById(TRAINEE_ID);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    @DisplayName("Should return an empty Optional when the trainee ID does not exist in storage")
    void findById_ShouldReturnNull_IsAbsent() {
        when(storage.get(TRAINEE_ID)).thenReturn(null);

        Optional<Trainee> result = traineeDao.findById(TRAINEE_ID);

        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should return a list containing all trainees available in storage")
    void findAll_ShouldReturnListOfAllTrainees() {
        Trainee t1 = Trainee.builder().id(TRAINEE_ID).build();
        Trainee t2 = Trainee.builder().id(2L).build();

        when(storage.values()).thenReturn(List.of(t1, t2));

        List<Trainee> actual = traineeDao.findAll();

        assertEquals(2, actual.size());
        assertTrue(actual.contains(t1));
        assertTrue(actual.contains(t2));
        verify(storage).values();
    }

    @Test
    @DisplayName("Should successfully remove trainee from storage by their ID")
    void delete_ShouldRemoveTraineeFromStorage() {
        traineeDao.delete(TRAINEE_ID);

        verify(storage).remove(TRAINEE_ID);
    }
}