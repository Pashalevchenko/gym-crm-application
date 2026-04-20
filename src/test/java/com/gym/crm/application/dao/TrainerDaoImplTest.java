package com.gym.crm.application.dao;

import com.gym.crm.application.dao.impl.TrainerDaoImpl;
import com.gym.crm.application.model.Trainer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainerDaoImplTest {

    @Mock
    private Map<Long, Trainer> storage;

    private final Long TRAINER_ID = 1L;
    private final Long UNEXIST_TRAINER_ID = 99L;

    private TrainerDaoImpl trainerDao;

    @BeforeEach
    void setUp() {
        trainerDao = new TrainerDaoImpl(storage);
    }

    @Test
    void create_ShouldStoreTrainer() {
        Trainer trainer = Trainer.builder()
                .id(TRAINER_ID)
                .firstName("Stepan")
                .lastName("Giga")
                .build();

        Trainer result = trainerDao.create(trainer);

        verify(storage).put(TRAINER_ID, trainer);
        assertEquals(trainer, result);
    }

    @Test
    void update_ShouldUpdateTrainer_WhenIdExists() {
        Trainer trainer = Trainer.builder()
                .id(TRAINER_ID)
                .firstName("Stepan")
                .lastName("Updated")
                .build();
        when(storage.containsKey(TRAINER_ID)).thenReturn(true);

        Trainer result = trainerDao.update(trainer);

        verify(storage).put(TRAINER_ID, trainer);
        assertEquals(trainer, result);
    }

    @Test
    void update_ShouldThrowException_WhenIdNotFound() {
        Trainer trainer = Trainer.builder().id(UNEXIST_TRAINER_ID).build();
        when(storage.containsKey(UNEXIST_TRAINER_ID)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                trainerDao.update(trainer)
        );

        assertTrue(exception.getMessage().contains("not found in storage"));
        verify(storage, never()).put(anyLong(), any());
    }

    @Test
    void findById_ShouldReturnTrainer_IfPresent() {
        Long id = 5L;
        Trainer trainer = Trainer.builder().id(TRAINER_ID).build();
        when(storage.get(TRAINER_ID)).thenReturn(trainer);

        Optional<Trainer> result = trainerDao.findById(TRAINER_ID);

        assertTrue(result.isPresent());
        assertEquals(trainer, result.get());
    }

    @Test
    void findById_ShouldReturnNull_IsAbsent() {
        Long id = 5L;
        when(storage.get(TRAINER_ID)).thenReturn(null);

        Optional<Trainer> result = trainerDao.findById(TRAINER_ID);

        assertFalse(result.isPresent());
    }

    @Test
    void findAll_ShouldReturnListOfAllTrainers() {
        Trainer t1 = Trainer.builder().id(1L).build();
        Trainer t2 = Trainer.builder().id(2L).build();

        when(storage.values()).thenReturn(List.of(t1, t2));

        List<Trainer> result = trainerDao.findAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(t1));
        assertTrue(result.contains(t2));
        verify(storage).values();
    }
}