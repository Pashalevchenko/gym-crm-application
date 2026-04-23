package com.gym.crm.application.dao;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.gym.crm.application.dao.impl.TrainerDaoImpl;
import com.gym.crm.application.model.Trainer;
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
public class TrainerDaoImplTest {

    private final Long TRAINER_ID = 1L;
    private final Long UNEXIST_TRAINER_ID = 99L;

    @Mock
    private Map<Long, Trainer> storage;

    private TrainerDao trainerDao;

    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        trainerDao = new TrainerDaoImpl(storage);

        Logger logger = (Logger) LoggerFactory.getLogger(TrainerDaoImpl.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    @DisplayName("Should successfully save a new trainer to storage and return the saved entity")
    void create_ShouldStoreTrainer() {
        Trainer expected = Trainer.builder()
                .userId(TRAINER_ID)
                .firstName("Stepan")
                .lastName("Giga")
                .build();

        Trainer actual = trainerDao.create(expected);

        verify(storage).put(TRAINER_ID, expected);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should successfully update trainer details when the trainer ID exists in storage")
    void update_ShouldUpdateTrainer_WhenIdExists() {
        Trainer expected = Trainer.builder()
                .userId(TRAINER_ID)
                .firstName("Stepan")
                .lastName("Updated")
                .build();
        String expectedLogMessage = String.format("Trainer with id: %d was update", expected.getUserId());

        when(storage.containsKey(TRAINER_ID)).thenReturn(true);

        Trainer actual = trainerDao.update(expected);

        verify(storage).put(TRAINER_ID, expected);
        assertEquals(expected, actual);
        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getFormattedMessage, ILoggingEvent::getLevel)
                .contains(tuple(expectedLogMessage, Level.INFO));
        assertThat(listAppender.list)
                .extracting(ILoggingEvent::getLevel)
                .doesNotContain(Level.ERROR);
    }

    @Test
    @DisplayName("Should throw RuntimeException when attempting to update a trainer that does not exist")
    void update_ShouldThrowException_WhenIdNotFound() {
        Trainer trainer = Trainer.builder().userId(UNEXIST_TRAINER_ID).build();

        when(storage.containsKey(UNEXIST_TRAINER_ID)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                trainerDao.update(trainer)
        );

        assertTrue(exception.getMessage().contains("not found in storage"));
        verify(storage, never()).put(anyLong(), any());
    }

    @Test
    @DisplayName("Should return an Optional containing the trainer when the provided ID exists")
    void findById_ShouldReturnTrainer_IfPresent() {
        Trainer expected = Trainer.builder().userId(TRAINER_ID).build();

        when(storage.get(TRAINER_ID)).thenReturn(expected);

        Optional<Trainer> actual = trainerDao.findById(TRAINER_ID);

        assertTrue(actual.isPresent());
        assertEquals(expected, actual.get());
    }

    @Test
    @DisplayName("Should return an empty Optional when the trainer ID does not exist in storage")
    void findById_ShouldReturnNull_IsAbsent() {
        when(storage.get(TRAINER_ID)).thenReturn(null);

        Optional<Trainer> actual = trainerDao.findById(TRAINER_ID);

        assertFalse(actual.isPresent());
    }

    @Test
    @DisplayName("Should return a list containing all trainers currently stored in the system")
    void findAll_ShouldReturnListOfAllTrainers() {
        Trainer t1 = Trainer.builder().userId(1L).build();
        Trainer t2 = Trainer.builder().userId(2L).build();

        when(storage.values()).thenReturn(List.of(t1, t2));

        List<Trainer> actual = trainerDao.findAll();

        assertEquals(2, actual.size());
        assertTrue(actual.contains(t1));
        assertTrue(actual.contains(t2));
        verify(storage).values();
    }
}