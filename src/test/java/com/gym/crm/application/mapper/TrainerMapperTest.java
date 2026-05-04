package com.gym.crm.application.mapper;

import com.gym.crm.application.dto.mapper.TrainerMapper;
import com.gym.crm.application.dto.request.TrainerRequestDTO;
import com.gym.crm.application.dto.response.TrainerResponseDTO;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.TrainingType;
import com.gym.crm.application.entity.User;
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
    @DisplayName("Should correctly map TrainerRequestDTO to Trainer entity including nested user and specialization")
    void dtoToEntity_shouldMapAllFieldsCorrectly() {
        TrainingType specialization = TrainingType.builder()
                .trainingTypeName("Boxing")
                .build();
        TrainerRequestDTO request = TrainerRequestDTO.builder()
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .isActive(true)
                .specialization(specialization)
                .build();
        Trainer actual = trainerMapper.dtoToEntity(request);

        assertNotNull(actual);
        assertNotNull(actual.getUser());
        assertEquals(USER_FIRST_NAME, actual.getUser().getFirstName());
        assertEquals(USER_LAST_NAME, actual.getUser().getLastName());
        assertTrue(actual.getUser().isActive());
        assertNotNull(actual.getSpecialization());
        assertEquals("Boxing", actual.getSpecialization().getTrainingTypeName());
    }

    @Test
    @DisplayName("Should correctly map Trainer entity to response DTO including user and specialization")
    void entityToDto_shouldMapAllFieldsCorrectly() {
        TrainingType specialization = TrainingType.builder()
                .trainingTypeName("Yoga")
                .build();
        User user = User.builder()
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .username(USERNAME)
                .isActive(false)
                .build();
        Trainer entity = Trainer.builder()
                .id(1L)
                .user(user)
                .specialization(specialization)
                .build();
        TrainerResponseDTO actual = trainerMapper.entityToDto(entity);

        assertNotNull(actual);
        assertEquals(USER_FIRST_NAME, actual.getFirstName());
        assertEquals(USER_LAST_NAME, actual.getLastName());
        assertEquals(USERNAME, actual.getUsername());
        assertNotNull(actual.getSpecialization());
        assertEquals("Yoga", actual.getSpecialization().getTrainingTypeName());
    }
}