package com.gym.crm.application.service;

import com.gym.crm.application.dao.TrainingTypeDao;
import com.gym.crm.application.entity.TrainingType;
import com.gym.crm.application.service.impl.TrainingTypeServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TrainingTypeServiceImplTest {

    @Mock
    private TrainingTypeDao trainingTypeDao;

    @InjectMocks
    private TrainingTypeServiceImpl trainingTypeService;

    @Test
    @DisplayName("Should return all training types")
    void getAllTrainingType_shouldReturnAllTrainingTypes() {
        TrainingType yoga = TrainingType.builder()
                .id(1L)
                .trainingTypeName("Yoga")
                .build();
        TrainingType cardio = TrainingType.builder()
                .id(2L)
                .trainingTypeName("Cardio")
                .build();
        List<TrainingType> expected = List.of(yoga, cardio);

        when(trainingTypeDao.findAll()).thenReturn(expected);

        List<TrainingType> actual = trainingTypeService.getAllTrainingsType();

        assertEquals(expected, actual);
        verify(trainingTypeDao).findAll();
    }
}