package com.gym.crm.application.service;

import com.gym.crm.application.dao.TrainingDao;
import com.gym.crm.application.model.Training;
import com.gym.crm.application.service.impl.TrainingServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TrainingServiceImplTest {

    @Mock
    private TrainingDao trainingDao;

    @InjectMocks
    private TrainingServiceImpl trainingService;

    @Test
    void createTraining_ShouldWorkCorrectly() {
        Training training = Training.builder()
                .trainingName("Crossfit")
                .build();

        when(trainingDao.create(training)).thenReturn(training);

        Training result = trainingService.createTraining(training);

        assertNotNull(result);
        assertEquals("Crossfit", result.getTrainingName());
        verify(trainingDao).create(training);
    }

    @Test
    void getTrainingById_WhenFound() {
        Long id = 10L;
        Training training = Training.builder()
                .trainingName("Yoga")
                .build();

        when(trainingDao.findById(id)).thenReturn(Optional.of(training));

        Training result = trainingService.getTrainingById(id);

        assertEquals("Yoga", result.getTrainingName());
        verify(trainingDao).findById(id);
    }

    @Test
    void getTrainingById_WhenNotFound() {
        Long id = 999L;

        when(trainingDao.findById(id)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class,
                () -> trainingService.getTrainingById(id));

        assertTrue(exception.getMessage().contains("ID 999 not found"));
    }

    @Test
    void getAllTrainings_ShouldReturnList() {
        List<Training> trainings = List.of(new Training(), new Training());

        when(trainingDao.findAll()).thenReturn(trainings);

        List<Training> result = trainingService.getAllTrainings();

        assertEquals(2, result.size());
        verify(trainingDao).findAll();
    }
}