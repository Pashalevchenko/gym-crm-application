package com.gym.crm.application.dao;

import com.gym.crm.application.dao.impl.TrainingDaoImpl;
import com.gym.crm.application.model.Training;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TrainingDaoImplTest {

    private final Long TRAINING_ID = 1L;

    @Mock
    private Map<Long, Training> storage;

    private TrainingDao trainingDao;

    @BeforeEach
    void setUp() {
        when(storage.isEmpty()).thenReturn(true);

        trainingDao = new TrainingDaoImpl(storage);
    }

    @Test
    @DisplayName("Should generate a unique ID and successfully store the training session")
    void create_ShouldGenerateIdAndSaveTraining() {
        Training expected = Training.builder()
                .trainingName("Yoga")
                .build();

        Training actual = trainingDao.create(expected);

        verify(storage).put(eq(2L), eq(expected));
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should return an Optional containing the training with correct data when ID exists")
    void findById_ShouldReturnTraining_WhenExists() {
        Training training = Training.builder().trainingName("Boxing").build();

        when(storage.get(TRAINING_ID)).thenReturn(training);

        Optional<Training> actual = trainingDao.findById(TRAINING_ID);

        assertTrue(actual.isPresent());
        assertEquals("Boxing", actual.get().getTrainingName());
    }

    @Test
    void findAll_ShouldReturnAllTrainingsFromStorage() {
        Training t1 = Training.builder().trainingName("T1").build();
        Training t2 = Training.builder().trainingName("T2").build();

        when(storage.values()).thenReturn(List.of(t1, t2));

        List<Training> actual = trainingDao.findAll();

        assertEquals(2, actual.size());
        verify(storage).values();
    }

    @Test
    @DisplayName("Should retrieve all training sessions currently stored in the system")
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