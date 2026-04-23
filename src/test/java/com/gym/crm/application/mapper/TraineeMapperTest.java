package com.gym.crm.application.mapper;

import com.gym.crm.application.dto.mapper.TraineeMapper;
import com.gym.crm.application.dto.request.TraineeRequestDTO;
import com.gym.crm.application.dto.response.TraineeResponseDTO;
import com.gym.crm.application.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TraineeMapperTest {

    private final String USER_FIRST_NAME = "Ivan";
    private final String USER_LAST_NAME = "Ivanov";
    private final String USERNAME = USER_FIRST_NAME + '.' + USER_LAST_NAME;

    private TraineeMapper traineeMapper;

    @BeforeEach
    void setUp() {
        traineeMapper = new TraineeMapper();
    }

    @Test
    @DisplayName("Should correctly map all provided fields from TraineeRequestDTO to Trainee entity")
    void dtoToEntity_ShouldMapAllFieldsCorrectly() {
        TraineeRequestDTO expected = TraineeRequestDTO.builder()
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .isActive(true)
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .address("Kyiv")
                .build();

        Trainee actual = traineeMapper.dtoToEntity(expected);

        assertNotNull(actual);
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.isActive(), actual.isActive());
        assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth());
        assertEquals(expected.getAddress(), actual.getAddress());
        assertNull(actual.getUsername());
        assertEquals(0, actual.getUserId());
    }

    @Test
    @DisplayName("Should correctly map all fields from Trainee entity to Response DTO including generated values")
    void entityToDto_ShouldMapAllFieldsCorrectly() {
        Trainee expected = Trainee.builder()
                .userId(100L)
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .username(USERNAME)
                .isActive(false)
                .dateOfBirth(LocalDate.of(1960, 4, 15))
                .address("Ivano-Frankivsk")
                .build();

        TraineeResponseDTO actual = traineeMapper.entityToDto(expected);

        assertNotNull(actual);
        assertEquals(expected.getUserId(), actual.getId());
        assertEquals(expected.getFirstName(), actual.getFirstName());
        assertEquals(expected.getLastName(), actual.getLastName());
        assertEquals(expected.getUsername(), actual.getUsername());
        assertEquals(expected.isActive(), actual.isActive());
        assertEquals(expected.getDateOfBirth(), actual.getDateOfBirth());
        assertEquals(expected.getAddress(), actual.getAddress());
    }
}