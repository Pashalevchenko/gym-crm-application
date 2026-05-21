package com.gym.crm.application.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.gym.crm.application.facade.GymAppFacade;
import com.gym.crm.application.openapi.ActivationStatusRequest;
import com.gym.crm.application.openapi.AssignedTrainerResponse;
import com.gym.crm.application.openapi.GetTraineeTrainingResponse;
import com.gym.crm.application.openapi.TraineeAssignedTrainersUpdateRequest;
import com.gym.crm.application.openapi.TraineeAssignedTrainersUpdateResponse;
import com.gym.crm.application.openapi.TraineeCreateRequest;
import com.gym.crm.application.openapi.TraineeCreateResponse;
import com.gym.crm.application.openapi.TraineeGetResponse;
import com.gym.crm.application.openapi.TraineeUpdateRequest;
import com.gym.crm.application.openapi.TraineeUpdateResponse;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import java.time.LocalDate;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TraineeControllerTest {

    private static final String BASE_URL = "/api/v1/trainees";
    private static final String USERNAME = "test.user";
    private static final String FIRST_NAME = "Test";
    private static final String LAST_NAME = "User";

    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private MockMvc mockMvc;

    @Mock
    private GymAppFacade facade;

    @BeforeEach
    void setUp() {
        TraineeController controller = new TraineeController(facade);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.setProviderClass(HibernateValidator.class);
        validator.setMessageInterpolator(new ParameterMessageInterpolator());
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setValidator(validator)
                .addPlaceholderValue("app.api.base-path", "/api/v1")
                .build();
    }

    @Test
    void register_shouldReturnOk() throws Exception {
        TraineeCreateRequest request = new TraineeCreateRequest()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Kyiv");
        TraineeCreateResponse response = new TraineeCreateResponse()
                .username(USERNAME)
                .password("password");

        when(facade.createTrainee(any(TraineeCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post(BASE_URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(facade).createTrainee(any(TraineeCreateRequest.class));
    }

    @Test
    void getTraineeProfile_shouldReturnOk() throws Exception {
        TraineeGetResponse response = new TraineeGetResponse()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Kyiv")
                .isActive(true);

        when(facade.getTraineeByUsername(USERNAME)).thenReturn(response);

        mockMvc.perform(get(BASE_URL + "/" + USERNAME))
                .andExpect(status().isOk());
        verify(facade).getTraineeByUsername(USERNAME);
    }

    @Test
    void updateTraineeProfile_shouldReturnOk() throws Exception {
        TraineeUpdateRequest request = new TraineeUpdateRequest()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Lviv")
                .isActive(false);
        TraineeUpdateResponse response = new TraineeUpdateResponse()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Lviv")
                .isActive(false);

        when(facade.updateTrainee(any(TraineeUpdateRequest.class), any(String.class)))
                .thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/" + USERNAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(facade).updateTrainee(any(TraineeUpdateRequest.class), any(String.class));
    }

    @Test
    void deleteTrainee_shouldReturnOk() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/" + USERNAME))
                .andExpect(status().isOk());

        verify(facade).deleteTraineeByUsername(USERNAME);
    }

    @Test
    void toggleActive_shouldReturnOk() throws Exception {
        ActivationStatusRequest request = new ActivationStatusRequest()
                .isActive(true);

        mockMvc.perform(patch(BASE_URL + "/" + USERNAME + "/activation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        verify(facade).changeActiveStatus(any(String.class), any(ActivationStatusRequest.class));
    }

    @Test
    void getTraineeTrainings_shouldReturnOk() throws Exception {
        GetTraineeTrainingResponse training = new GetTraineeTrainingResponse()
                .trainingName("Morning Yoga")
                .trainingDate(LocalDate.of(2026, 4, 10))
                .trainingDuration(60)
                .trainingType("Yoga")
                .trainerName("trainer.user");

        when(facade.getTraineeTrainings(any(String.class),
                                        any(LocalDate.class),
                                        any(LocalDate.class),
                                        any(String.class),
                                        any(String.class)))
                .thenReturn(List.of(training));

        mockMvc.perform(get(BASE_URL + "/" + USERNAME + "/trainings")
                        .param("fromDate", "2026-04-01")
                        .param("toDate", "2026-04-30")
                        .param("trainerName", "Trainer")
                        .param("trainingType", "Yoga"))
                .andExpect(status().isOk());

        verify(facade).getTraineeTrainings(USERNAME, LocalDate.of(2026, 4, 1), LocalDate.of(2026, 4, 30), "Trainer", "Yoga");
    }

    @Test
    void updateTraineeTrainers_shouldReturnOk() throws Exception {
        TraineeAssignedTrainersUpdateRequest request =
                new TraineeAssignedTrainersUpdateRequest()
                        .trainerUsernames(List.of("trainer.user"));
        AssignedTrainerResponse trainerResponse = new AssignedTrainerResponse()
                .username("trainer.user")
                .firstName("Trainer")
                .lastName("User")
                .specialization("Yoga");
        TraineeAssignedTrainersUpdateResponse response =
                new TraineeAssignedTrainersUpdateResponse()
                        .trainers(List.of(trainerResponse));

        when(facade.updateTraineeTrainersList(any(String.class), any(TraineeAssignedTrainersUpdateRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put(BASE_URL + "/" + USERNAME + "/trainers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(facade).updateTraineeTrainersList(any(String.class), any(TraineeAssignedTrainersUpdateRequest.class));
    }

    @Test
    void getAvailableTrainers_shouldReturnOk() throws Exception {
        AssignedTrainerResponse trainer = new AssignedTrainerResponse()
                .username("trainer.user")
                .firstName("Trainer")
                .lastName("User")
                .specialization("Yoga");

        when(facade.getNotAssignedTrainers(USERNAME)).thenReturn(List.of(trainer));

        mockMvc.perform(get(BASE_URL + "/" + USERNAME + "/available-trainers"))
                .andExpect(status().isOk());
        verify(facade).getNotAssignedTrainers(USERNAME);
    }
}