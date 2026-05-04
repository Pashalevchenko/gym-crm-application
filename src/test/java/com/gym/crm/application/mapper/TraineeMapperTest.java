package com.gym.crm.application.mapper;

import com.gym.crm.application.dto.mapper.TraineeMapper;
import com.gym.crm.application.dto.request.TraineeRequestDTO;
import com.gym.crm.application.dto.response.TraineeResponseDTO;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class TraineeMapperTest {

    private static final Long TRAINEE_ID = 100L;
    private static final String USER_FIRST_NAME = "Ivan";
    private static final String USER_LAST_NAME = "Ivanov";
    private static final String USERNAME = USER_FIRST_NAME + '.' + USER_LAST_NAME;

    private TraineeMapper traineeMapper;

    @BeforeEach
    void setUp() {
        traineeMapper = new TraineeMapper();
    }

    @Test
    @DisplayName("Should correctly map all provided fields from TraineeRequestDTO to Trainee entity")
    void dtoToEntity_shouldMapAllFieldsCorrectly() {
        TraineeRequestDTO request = TraineeRequestDTO.builder()
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .isActive(true)
                .dateOfBirth(LocalDate.of(1995, 1, 1))
                .address("Kyiv")
                .build();

        Trainee actual = traineeMapper.dtoToEntity(request);

        assertNotNull(actual);
        assertNotNull(actual.getUser());
        assertEquals(request.getFirstName(), actual.getUser().getFirstName());
        assertEquals(request.getLastName(), actual.getUser().getLastName());
        assertEquals(request.isActive(), actual.getUser().isActive());
        assertEquals(request.getDateOfBirth(), actual.getDateOfBirth());
        assertEquals(request.getAddress(), actual.getAddress());

        assertNull(actual.getId());
        assertNull(actual.getUser().getUsername());
        assertNull(actual.getUser().getPassword());
    }

    @Test
    @DisplayName("Should correctly map all fields from Trainee entity to Response DTO including generated values")
    void entityToDto_shouldMapAllFieldsCorrectly() {
        User user = User.builder()
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .username(USERNAME)
                .isActive(false)
                .build();
        Trainee trainee = Trainee.builder()
                .id(TRAINEE_ID)
                .user(user)
                .dateOfBirth(LocalDate.of(1960, 4, 15))
                .address("Ivano-Frankivsk")
                .build();

        TraineeResponseDTO actual = traineeMapper.entityToDto(trainee);

        assertNotNull(actual);
        assertEquals(trainee.getId(), actual.getId());
        assertEquals(trainee.getUser().getFirstName(), actual.getFirstName());
        assertEquals(trainee.getUser().getLastName(), actual.getLastName());
        assertEquals(trainee.getUser().getUsername(), actual.getUsername());
        assertEquals(trainee.getUser().isActive(), actual.isActive());
        assertEquals(trainee.getDateOfBirth(), actual.getDateOfBirth());
        assertEquals(trainee.getAddress(), actual.getAddress());
    }
}