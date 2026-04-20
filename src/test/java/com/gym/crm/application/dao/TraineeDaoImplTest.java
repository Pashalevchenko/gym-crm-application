package com.gym.crm.application.dao;

import com.gym.crm.application.dao.impl.TraineeDaoImpl;
import com.gym.crm.application.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Map;
import java.util.Optional;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TraineeDaoImplTest {

    @Mock
    private Map<Long, Trainee> storage;

    private final Long TRAINEE_ID = 1L;
    private final Long UNEXIST_TRAINEE_ID = 99L;

    private TraineeDaoImpl traineeDao;

    @BeforeEach
    void setUp() {
        traineeDao = new TraineeDaoImpl(storage);
    }

    @Test
    void create_ShouldStoreTrainee() {
        Trainee trainee = Trainee.builder().id(TRAINEE_ID).firstName("Ivan").build();
        Trainee result = traineeDao.create(trainee);

        verify(storage).put(TRAINEE_ID, trainee);
        assertEquals(trainee, result);
    }

    @Test
    void update_ShouldUpdateTrainee_WhenIdExists() {
        Trainee trainee = Trainee.builder().id(TRAINEE_ID).firstName("Updated Name").build();

        when(storage.containsKey(TRAINEE_ID)).thenReturn(true);

        Trainee result = traineeDao.update(trainee);

        verify(storage).put(TRAINEE_ID, trainee);
        assertEquals(trainee, result);
    }

    @Test
    void update_ShouldThrowException_WhenIdDoesNotExist() {
        Trainee trainee = Trainee.builder().id(UNEXIST_TRAINEE_ID).build();

        when(storage.containsKey(UNEXIST_TRAINEE_ID)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                traineeDao.update(trainee)
        );

        assertTrue(exception.getMessage().contains("not found in storage"));
        verify(storage, never()).put(anyLong(), any());
    }

    @Test
    void findById_ShouldReturnTrainee_WhenIdExists() {
        Trainee trainee = Trainee.builder().id(TRAINEE_ID).build();

        when(storage.get(TRAINEE_ID)).thenReturn(trainee);

        Optional<Trainee> result = traineeDao.findById(TRAINEE_ID);

        assertTrue(result.isPresent());
        assertEquals(trainee, result.get());
    }

    @Test
    void findById_ShouldReturnNull_IsAbsent() {
        when(storage.get(TRAINEE_ID)).thenReturn(null);

        Optional<Trainee> result = traineeDao.findById(TRAINEE_ID);

        assertFalse(result.isPresent());
    }

    @Test
    void findAll_ShouldReturnListOfAllTrainees() {
        Trainee t1 = Trainee.builder().id(TRAINEE_ID).build();
        Trainee t2 = Trainee.builder().id(2L).build();

        when(storage.values()).thenReturn(List.of(t1, t2));

        List<Trainee> result = traineeDao.findAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(t1));
        assertTrue(result.contains(t2));
        verify(storage).values();
    }

    @Test
    void delete_ShouldRemoveTraineeFromStorage() {
        traineeDao.delete(TRAINEE_ID);

        verify(storage).remove(TRAINEE_ID);
    }
}