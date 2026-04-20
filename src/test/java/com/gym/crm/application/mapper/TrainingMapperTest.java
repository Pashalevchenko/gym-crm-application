package com.gym.crm.application.mapper;

import com.gym.crm.application.dto.mapper.TrainingMapper;
import com.gym.crm.application.dto.request.TrainingRequestDTO;
import com.gym.crm.application.dto.response.TrainingResponseDTO;
import com.gym.crm.application.model.Training;
import com.gym.crm.application.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class TrainingMapperTest {

    private final Long ENTITY_ID = 1L;

    private TrainingMapper trainingMapper;


    @BeforeEach
    void setUp() {
        trainingMapper = new TrainingMapper();
    }

    @Test
    void dtoToEntity_ShouldMapAllFieldsCorrectly() {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Strength");

        TrainingRequestDTO request = TrainingRequestDTO.builder()
                .traineeId(ENTITY_ID)
                .trainerId(ENTITY_ID)
                .trainingName("Deadlift Session")
                .trainingType(type)
                .trainingDate(LocalDate.of(2026, 5, 20))
                .trainingDuration(90)
                .build();

        Training entity = trainingMapper.dtoToEntity(request);

        assertNotNull(entity);
        assertEquals(request.getTraineeId(), entity.getTraineeId());
        assertEquals(request.getTrainerId(), entity.getTrainerId());
        assertEquals(request.getTrainingName(), entity.getTrainingName());
        assertEquals(request.getTrainingType(), entity.getTrainingType());
        assertEquals(request.getTrainingDate(), entity.getTrainingDate());
        assertEquals(request.getTrainingDuration(), entity.getTrainingDuration());
    }

    @Test
    void entityToDto_ShouldMapAllFieldsCorrectly() {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Cardio");

        Training entity = Training.builder()
                .traineeId(ENTITY_ID)
                .trainerId(ENTITY_ID)
                .trainingName("Morning Run")
                .trainingType(type)
                .trainingDate(LocalDate.now())
                .trainingDuration(45)
                .build();

        TrainingResponseDTO response = trainingMapper.entityToDto(entity);

        assertNotNull(response);
        assertEquals(entity.getTraineeId(), response.getTraineeId());
        assertEquals(entity.getTrainerId(), response.getTrainerId());
        assertEquals(entity.getTrainingName(), response.getTrainingName());
        assertEquals(entity.getTrainingType(), response.getTrainingType());
        assertEquals(entity.getTrainingDate(), response.getTrainingDate());
        assertEquals(entity.getTrainingDuration(), response.getTrainingDuration());
    }
}