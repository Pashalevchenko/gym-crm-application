package com.gym.crm.application.mapper;

import com.gym.crm.application.dto.mapper.TrainerMapper;
import com.gym.crm.application.dto.request.TrainerRequestDTO;
import com.gym.crm.application.dto.response.TrainerResponseDTO;
import com.gym.crm.application.model.Trainer;
import com.gym.crm.application.model.TrainingType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
    void dtoToEntity_ShouldMapAllFieldsCorrectly() {
        TrainingType spec = new TrainingType();
        spec.setTrainingTypeName("Boxing");

        TrainerRequestDTO request = TrainerRequestDTO.builder()
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .isActive(true)
                .specialization(spec)
                .build();

        Trainer entity = trainerMapper.dtoToEntity(request);

        assertNotNull(entity);
        assertEquals(USER_FIRST_NAME, entity.getFirstName());
        assertEquals(USER_LAST_NAME, entity.getLastName());
        assertTrue(entity.isActive());
        assertNotNull(entity.getSpecialization());
        assertEquals("Boxing", entity.getSpecialization().getTrainingTypeName());
    }

    @Test
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

        TrainerResponseDTO response = trainerMapper.entityToDto(entity);

        assertNotNull(response);
        assertEquals(USERNAME, response.getUsername());
        assertNotNull(response.getSpecialization());
        assertEquals("Yoga", response.getSpecialization().getTrainingTypeName());
    }
}