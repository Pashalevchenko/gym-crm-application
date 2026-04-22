package com.gym.crm.application.mapper;

import com.gym.crm.application.dto.mapper.TrainingMapper;
import com.gym.crm.application.dto.request.TrainingRequestDTO;
import com.gym.crm.application.dto.response.TrainingResponseDTO;
import com.gym.crm.application.model.Training;
import com.gym.crm.application.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TrainingMapperTest {

    private final Long ENTITY_ID = 1L;

    private TrainingMapper trainingMapper;

    @BeforeEach
    void setUp() {
        trainingMapper = new TrainingMapper();
    }

    @Test
    @DisplayName("Should correctly map TrainingRequestDTO to Training entity with all relational IDs and session details")
    void dtoToEntity_ShouldMapAllFieldsCorrectly() {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Strength");

        TrainingRequestDTO expected = TrainingRequestDTO.builder()
                .traineeId(ENTITY_ID)
                .trainerId(ENTITY_ID)
                .trainingName("Deadlift Session")
                .trainingType(type)
                .trainingDate(LocalDate.of(2026, 5, 20))
                .trainingDuration(90)
                .build();

        Training actual = trainingMapper.dtoToEntity(expected);

        assertNotNull(actual);
        assertEquals(expected.getTraineeId(), actual.getTraineeId());
        assertEquals(expected.getTrainerId(), actual.getTrainerId());
        assertEquals(expected.getTrainingName(), actual.getTrainingName());
        assertEquals(expected.getTrainingType(), actual.getTrainingType());
        assertEquals(expected.getTrainingDate(), actual.getTrainingDate());
        assertEquals(expected.getTrainingDuration(), actual.getTrainingDuration());
    }

    @Test
    @DisplayName("Should correctly map Training entity to Response DTO including all session details and participant IDs")
    void entityToDto_ShouldMapAllFieldsCorrectly() {
        TrainingType type = new TrainingType();
        type.setTrainingTypeName("Cardio");

        Training expected = Training.builder()
                .traineeId(ENTITY_ID)
                .trainerId(ENTITY_ID)
                .trainingName("Morning Run")
                .trainingType(type)
                .trainingDate(LocalDate.now())
                .trainingDuration(45)
                .build();

        TrainingResponseDTO actual = trainingMapper.entityToDto(expected);

        assertNotNull(actual);
        assertEquals(expected.getTraineeId(), actual.getTraineeId());
        assertEquals(expected.getTrainerId(), actual.getTrainerId());
        assertEquals(expected.getTrainingName(), actual.getTrainingName());
        assertEquals(expected.getTrainingType(), actual.getTrainingType());
        assertEquals(expected.getTrainingDate(), actual.getTrainingDate());
        assertEquals(expected.getTrainingDuration(), actual.getTrainingDuration());
    }
}