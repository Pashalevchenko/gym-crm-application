package com.gym.crm.application.mapper;

import com.gym.crm.application.dto.mapper.TraineeMapper;
import com.gym.crm.application.dto.request.TraineeRequestDTO;
import com.gym.crm.application.dto.response.TraineeResponseDTO;
import com.gym.crm.application.model.Trainee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

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
    void dtoToEntity_ShouldMapAllFieldsCorrectly() {
        TraineeRequestDTO request = TraineeRequestDTO.builder()
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .isActive(true)
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .address("Kyiv")
                .build();

        Trainee entity = traineeMapper.dtoToEntity(request);

        assertNotNull(entity);
        assertEquals(request.getFirstName(), entity.getFirstName());
        assertEquals(request.getLastName(), entity.getLastName());
        assertEquals(request.isActive(), entity.isActive());
        assertEquals(request.getDateOfBirth(), entity.getDateOfBirth());
        assertEquals(request.getAddress(), entity.getAddress());
        assertNull(entity.getUsername());
        assertEquals(0, entity.getId());
    }

    @Test
    void entityToDto_ShouldMapAllFieldsCorrectly() {
        Trainee entity = Trainee.builder()
                .id(100L)
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .username(USERNAME)
                .isActive(false)
                .dateOfBirth(LocalDate.of(1960, 4, 15))
                .address("Ivano-Frankivsk")
                .build();

        TraineeResponseDTO response = traineeMapper.entityToDto(entity);

        assertNotNull(response);
        assertEquals(entity.getId(), response.getId());
        assertEquals(entity.getFirstName(), response.getFirstName());
        assertEquals(entity.getLastName(), response.getLastName());
        assertEquals(entity.getUsername(), response.getUsername());
        assertEquals(entity.isActive(), response.isActive());
        assertEquals(entity.getDateOfBirth(), response.getDateOfBirth());
        assertEquals(entity.getAddress(), response.getAddress());
    }
}