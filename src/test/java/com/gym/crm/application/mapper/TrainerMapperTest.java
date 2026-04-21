package com.gym.crm.application.mapper;

import com.gym.crm.application.dto.mapper.TrainerMapper;
import com.gym.crm.application.dto.request.TrainerRequestDTO;
import com.gym.crm.application.dto.response.TrainerResponseDTO;
import com.gym.crm.application.model.Trainer;
import com.gym.crm.application.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrainerMapperTest {

    private final String USER_FIRST_NAME = "Ivan";
    private final String USER_LAST_NAME = "Ivanov";
    private final String USERNAME = USER_FIRST_NAME + '.' + USER_LAST_NAME;

    private TrainerMapper trainerMapper;

    @BeforeEach
    void setUp() {
        trainerMapper = new TrainerMapper();
    }

    @Test
    @DisplayName("Should correctly map TrainerRequestDTO to Trainer entity including nested specialization details")
    void dtoToEntity_ShouldMapAllFieldsCorrectly() {
        TrainingType spec = new TrainingType();
        spec.setTrainingTypeName("Boxing");

        TrainerRequestDTO request = TrainerRequestDTO.builder()
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .isActive(true)
                .specialization(spec)
                .build();

        Trainer actual = trainerMapper.dtoToEntity(request);

        assertNotNull(actual);
        assertEquals(USER_FIRST_NAME, actual.getFirstName());
        assertEquals(USER_LAST_NAME, actual.getLastName());
        assertTrue(actual.isActive());
        assertNotNull(actual.getSpecialization());
        assertEquals("Boxing", actual.getSpecialization().getTrainingTypeName());
    }

    @Test
    @DisplayName("Should correctly map Trainer entity to Response DTO including username and specialization details")
    void entityToDto_ShouldMapAllFieldsCorrectly() {
        TrainingType spec = new TrainingType();
        spec.setTrainingTypeName("Yoga");

        Trainer entity = Trainer.builder()
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .username(USERNAME)
                .isActive(false)
                .specialization(spec)
                .build();

        TrainerResponseDTO actual = trainerMapper.entityToDto(entity);

        assertNotNull(actual);
        assertEquals(USERNAME, actual.getUsername());
        assertNotNull(actual.getSpecialization());
        assertEquals("Yoga", actual.getSpecialization().getTrainingTypeName());
    }
}