package com.gym.crm.application.dao;

import com.gym.crm.application.dao.impl.TrainingDaoImpl;
import com.gym.crm.application.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingDaoImplTest {

    @Mock
    private Map<Long, Training> storage;

    private final Long TRAINING_ID = 1L;

    private TrainingDaoImpl trainingDao;

    @BeforeEach
    void setUp() {
        when(storage.isEmpty()).thenReturn(true);
        trainingDao = new TrainingDaoImpl(storage);
    }

    @Test
    void create_ShouldGenerateIdAndSaveTraining() {
        Training training = Training.builder()
                .trainingName("Yoga")
                .build();

        Training result = trainingDao.create(training);

        verify(storage).put(eq(2L), eq(training));
        assertEquals(training, result);
    }

    @Test
    void findById_ShouldReturnTraining_WhenExists() {
        Training training = Training.builder().trainingName("Boxing").build();
        when(storage.get(TRAINING_ID)).thenReturn(training);

        Optional<Training> result = trainingDao.findById(TRAINING_ID);

        assertTrue(result.isPresent());
        assertEquals("Boxing", result.get().getTrainingName());
    }

    @Test
    void findAll_ShouldReturnAllTrainingsFromStorage() {
        Training t1 = Training.builder().trainingName("T1").build();
        Training t2 = Training.builder().trainingName("T2").build();
        when(storage.values()).thenReturn(List.of(t1, t2));

        List<Training> result = trainingDao.findAll();

        assertEquals(2, result.size());
        verify(storage).values();
    }

    @Test
    void syncStorageId_ShouldSetGeneratorToMaxId_WhenStorageIsNotEmpty() {
        Map<Long, Training> realStorage = new HashMap<>();
        realStorage.put(10L, Training.builder().build());
        realStorage.put(50L, Training.builder().build());

        TrainingDaoImpl daoWithData = new TrainingDaoImpl(realStorage);

        Training newTraining = Training.builder().trainingName("New").build();
        daoWithData.create(newTraining);

        assertNotNull(realStorage.get(51L));
        assertEquals("New", realStorage.get(51L).getTrainingName());
    }
}