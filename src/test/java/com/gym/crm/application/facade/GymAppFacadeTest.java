package com.gym.crm.application.facade;

import com.gym.crm.application.dto.mapper.TraineeMapper;
import com.gym.crm.application.dto.mapper.TrainerMapper;
import com.gym.crm.application.dto.mapper.TrainingMapper;
import com.gym.crm.application.dto.mapper.rest.TraineeRestMapper;
import com.gym.crm.application.dto.request.TraineeUpdateDTO;
import com.gym.crm.application.dto.request.TrainerRequestDTO;
import com.gym.crm.application.dto.request.TrainerUpdateDTO;
import com.gym.crm.application.dto.request.TrainingRequestDTO;
import com.gym.crm.application.dto.response.TraineeResponseDTO;
import com.gym.crm.application.dto.response.TrainerResponseDTO;
import com.gym.crm.application.dto.response.TrainingResponseDTO;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.Training;
import com.gym.crm.application.entity.TrainingType;
import com.gym.crm.application.entity.User;
import com.gym.crm.application.openapi.ActivationStatusRequest;
import com.gym.crm.application.openapi.AssignedTrainerResponse;
import com.gym.crm.application.openapi.GetTraineeTrainingResponse;
import com.gym.crm.application.openapi.LoginChangeRequest;
import com.gym.crm.application.openapi.TraineeAssignedTrainersUpdateRequest;
import com.gym.crm.application.openapi.TraineeAssignedTrainersUpdateResponse;
import com.gym.crm.application.openapi.TraineeCreateRequest;
import com.gym.crm.application.openapi.TraineeCreateResponse;
import com.gym.crm.application.openapi.TraineeGetResponse;
import com.gym.crm.application.openapi.TraineeUpdateRequest;
import com.gym.crm.application.openapi.TraineeUpdateResponse;
import com.gym.crm.application.service.TraineeService;
import com.gym.crm.application.service.TrainerService;
import com.gym.crm.application.service.TrainingService;
import com.gym.crm.application.service.UserService;
import com.gym.crm.application.service.common.AuthenticationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import com.gym.crm.application.context.SecurityContextHolder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class GymAppFacadeTest {

    private static final String FIRST_NAME = "Ivan";
    private static final String LAST_NAME = "Ivanov";
    private static final String USERNAME = FIRST_NAME + "." + LAST_NAME;
    private static final String NEW_PASSWORD = "new12345";
    private static final Long TRAINEE_ID = 1L;
    private static final Long TRAINER_ID = 2L;
    private static final Long TRAINING_ID = 3L;

    @Mock
    private TraineeService traineeService;

    @Mock
    private TrainerService trainerService;

    @Mock
    private TrainingService trainingService;

    @Mock
    private TraineeMapper traineeMapper;

    @Mock
    private TraineeRestMapper traineeRestMapper;

    @Mock
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingMapper trainingMapper;

    @Mock
    private AuthenticationService authService;

    @Mock
    private UserService userService;

    @InjectMocks
    private GymAppFacade facade;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clear();
    }

    @Test
    void loginShouldAuthenticateUserAndSetSecurityContext() {
        String username = "test.user";
        String password = "12345";

        facade.login(username, password);

        verify(authService).authenticate(username, password);
        assertThat(SecurityContextHolder.getContext()).isEqualTo(username);
    }

    @Test
    void loginShouldNotSetSecurityContextWhenAuthenticationFails() {
        String username = "test.user";
        String password = "wrong-password";

        doThrow(new IllegalArgumentException("Invalid username or password"))
                .when(authService)
                .authenticate(username, password);

        assertThatThrownBy(() -> facade.login(username, password))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid username or password");

        assertThat(SecurityContextHolder.getContext()).isNull();
        verify(authService).authenticate(username, password);
    }

    @Test
    void logoutShouldClearSecurityContext() {
        SecurityContextHolder.setContext("test.user");

        facade.logout();

        assertThat(SecurityContextHolder.getContext()).isNull();
    }

    @Test
    @DisplayName("Verify that facade calls trainee service and uses rest mapper for create operation")
    void createTrainee_Test() {
        TraineeCreateRequest request = new TraineeCreateRequest()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME);
        Trainee trainee = Trainee.builder().build();
        User user = User.builder()
                .username(USERNAME)
                .password("generated-password")
                .build();
        Trainee createdTrainee = Trainee.builder()
                .id(TRAINEE_ID)
                .user(user)
                .build();
        TraineeCreateResponse expected = new TraineeCreateResponse()
                .username(USERNAME)
                .password("generated-password");

        when(traineeRestMapper.toEntity(request)).thenReturn(trainee);
        when(traineeService.createTrainee(trainee)).thenReturn(createdTrainee);
        when(traineeRestMapper.toCreateResponse(createdTrainee)).thenReturn(expected);

        TraineeCreateResponse actual = facade.createTrainee(request);

        assertEquals(expected, actual);
        verify(traineeRestMapper).toEntity(request);
        verify(traineeService).createTrainee(trainee);
        verify(traineeRestMapper).toCreateResponse(createdTrainee);
    }

    @Test
    @DisplayName("Should return trainee DTO when a valid ID is provided to the facade")
    void getTraineeById_Test() {
        Trainee trainee = Trainee.builder().id(TRAINEE_ID).build();
        TraineeResponseDTO expected = TraineeResponseDTO.builder()
                .id(TRAINEE_ID)
                .firstName(FIRST_NAME)
                .build();

        when(traineeService.getTraineeById(TRAINEE_ID)).thenReturn(trainee);
        when(traineeMapper.entityToDto(trainee)).thenReturn(expected);

        TraineeResponseDTO actual = facade.getTraineeById(TRAINEE_ID);

        assertEquals(expected, actual);
        verify(traineeService).getTraineeById(TRAINEE_ID);
        verify(traineeMapper).entityToDto(trainee);
    }

    @Test
    @DisplayName("Should get trainee by username")
    void getTraineeByUsername_shouldReturnMappedDto() {
        Trainee trainee = Trainee.builder().id(TRAINEE_ID).build();
        TraineeGetResponse expected = new TraineeGetResponse()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(true);

        when(traineeService.getTraineeByUsername(USERNAME)).thenReturn(trainee);
        when(traineeRestMapper.toGetResponse(trainee)).thenReturn(expected);

        TraineeGetResponse actual = facade.getTraineeByUsername(USERNAME);

        assertEquals(expected, actual);
        verify(traineeService).getTraineeByUsername(USERNAME);
        verify(traineeRestMapper).toGetResponse(trainee);
    }

    @Test
    @DisplayName("Should get all trainees")
    void getAllTrainees_shouldReturnMappedDtoList() {
        Trainee trainee = Trainee.builder().id(TRAINEE_ID).build();
        TraineeResponseDTO response = TraineeResponseDTO.builder()
                .id(TRAINEE_ID)
                .username(USERNAME)
                .build();

        when(traineeService.getAllTrainees()).thenReturn(List.of(trainee));
        when(traineeMapper.entityToDto(trainee)).thenReturn(response);

        List<TraineeResponseDTO> actual = facade.getAllTrainees();

        assertEquals(1, actual.size());
        assertEquals(USERNAME, actual.get(0).getUsername());
        verify(traineeService).getAllTrainees();
        verify(traineeMapper).entityToDto(trainee);
    }

    @Test
    @DisplayName("Should successfully update trainee by mapping request DTO to entity and returning response DTO")
    void updateTrainee_Test() {
        TraineeUpdateRequest request = new TraineeUpdateRequest()
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(false);
        TraineeUpdateDTO dto = TraineeUpdateDTO.builder()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(false)
                .build();
        Trainee trainee = Trainee.builder()
                .id(TRAINEE_ID)
                .build();
        Trainee updated = Trainee.builder()
                .id(TRAINEE_ID)
                .build();
        TraineeUpdateResponse expected = new TraineeUpdateResponse()
                .username(USERNAME)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .isActive(false);

        when(traineeRestMapper.toUpdateDto(USERNAME, request)).thenReturn(dto);
        when(traineeMapper.dtoToEntity(dto)).thenReturn(trainee);
        when(traineeService.updateTrainee(trainee)).thenReturn(updated);
        when(traineeRestMapper.toUpdateResponse(updated)).thenReturn(expected);

        TraineeUpdateResponse actual = facade.updateTrainee(request, USERNAME);

        assertEquals(expected, actual);
        verify(traineeRestMapper).toUpdateDto(USERNAME, request);
        verify(traineeMapper).dtoToEntity(dto);
        verify(traineeService).updateTrainee(trainee);
        verify(traineeRestMapper).toUpdateResponse(updated);
    }

    @Test
    @DisplayName("Should change trainee password")
    void changeTraineePassword_shouldDelegateToService() {
        facade.changeTraineePassword(USERNAME, NEW_PASSWORD);

        verify(traineeService).changePassword(USERNAME, NEW_PASSWORD);
    }

    @Test
    @DisplayName("Should change trainee active status to active")
    void changeActiveStatus_shouldActivateTrainee() {
        ActivationStatusRequest request = new ActivationStatusRequest()
                .isActive(true);

        facade.changeActiveStatus(USERNAME, request);

        verify(traineeService).changeActiveStatus(USERNAME, true);
    }

    @Test
    @DisplayName("Should change trainee active status to inactive")
    void changeActiveStatus_shouldDeactivateTrainee() {
        ActivationStatusRequest request = new ActivationStatusRequest()
                .isActive(false);

        facade.changeActiveStatus(USERNAME, request);

        verify(traineeService).changeActiveStatus(USERNAME, false);
    }

    @Test
    @DisplayName("Should successfully delegate trainee deletion to the service layer using the provided ID")
    void deleteTrainee_Test() {
        facade.deleteTrainee(TRAINEE_ID);

        verify(traineeService).deleteTrainee(TRAINEE_ID);
    }

    @Test
    @DisplayName("Should delete trainee by username")
    void deleteTraineeByUsername_Test() {
        facade.deleteTraineeByUsername(USERNAME);

        verify(traineeService).deleteTraineeByUsername(USERNAME);
    }

    @Test
    @DisplayName("Should get trainee trainings")
    void getTraineeTrainings_shouldReturnMappedTrainingList() {
        LocalDate fromDate = LocalDate.of(2026, 1, 1);
        LocalDate toDate = LocalDate.of(2026, 1, 31);
        String trainerName = "Ivan Trainer";
        String trainingTypeName = "Yoga";

        Training training = Training.builder()
                .id(TRAINING_ID)
                .trainingName("Morning Yoga")
                .build();

        GetTraineeTrainingResponse response = new GetTraineeTrainingResponse()
                .trainingName("Morning Yoga");

        when(traineeService.getTraineeTrainings(USERNAME, fromDate, toDate, trainerName, trainingTypeName))
                .thenReturn(List.of(training));
        when(traineeRestMapper.toTrainingResponses(List.of(training)))
                .thenReturn(List.of(response));

        List<GetTraineeTrainingResponse> actual =
                facade.getTraineeTrainings(USERNAME, fromDate, toDate, trainerName, trainingTypeName);

        assertEquals(1, actual.size());
        assertEquals("Morning Yoga", actual.get(0).getTrainingName());

        verify(traineeService).getTraineeTrainings(USERNAME, fromDate, toDate, trainerName, trainingTypeName);
        verify(traineeRestMapper).toTrainingResponses(List.of(training));
    }

    @Test
    @DisplayName("Should get not assigned trainers")
    void getNotAssignedTrainers_shouldReturnMappedTrainerList() {
        Trainer trainer = Trainer.builder()
                .id(TRAINER_ID)
                .build();
        AssignedTrainerResponse response = new AssignedTrainerResponse()
                .username("trainer.username")
                .firstName("Trainer")
                .lastName("Test");

        when(traineeService.getNotAssignedTrainers(USERNAME)).thenReturn(List.of(trainer));
        when(traineeRestMapper.toAssignedTrainerResponses(List.of(trainer))).thenReturn(List.of(response));

        List<AssignedTrainerResponse> actual = facade.getNotAssignedTrainers(USERNAME);

        assertEquals(1, actual.size());
        assertEquals("trainer.username", actual.get(0).getUsername());
        verify(traineeService).getNotAssignedTrainers(USERNAME);
        verify(traineeRestMapper).toAssignedTrainerResponses(List.of(trainer));
    }

    @Test
    @DisplayName("Should update trainee trainers list")
    void updateTraineeTrainersList_shouldDelegateToServiceAndMapResponse() {
        String trainerUsername = "trainer.test";
        User user = User.builder()
                .username(trainerUsername)
                .firstName("Trainer")
                .lastName("Test")
                .build();
        TraineeAssignedTrainersUpdateRequest request =
                new TraineeAssignedTrainersUpdateRequest()
                        .trainerUsernames(List.of(trainerUsername));

        Trainer trainer = Trainer.builder()
                .id(TRAINER_ID)
                .user(user)
                .build();
        Set<Trainer> trainers = Set.of(trainer);
        Trainee updatedTrainee = Trainee.builder()
                .id(TRAINEE_ID)
                .trainers(trainers)
                .build();
        AssignedTrainerResponse trainerResponse = new AssignedTrainerResponse()
                .username(trainerUsername)
                .firstName("Trainer")
                .lastName("Test");
        TraineeAssignedTrainersUpdateResponse expected =
                new TraineeAssignedTrainersUpdateResponse()
                        .trainers(List.of(trainerResponse));

        when(trainerService.getTrainerByUsername(trainerUsername)).thenReturn(trainer);
        when(traineeService.updateTrainersList(USERNAME, trainers)).thenReturn(updatedTrainee);
        when(traineeRestMapper.toAssignedTrainerResponse(trainer)).thenReturn(trainerResponse);
        when(traineeRestMapper.toAssignedTrainersUpdateResponse(List.of(trainerResponse))).thenReturn(expected);

        TraineeAssignedTrainersUpdateResponse actual = facade.updateTraineeTrainersList(USERNAME, request);

        assertEquals(expected, actual);

        verify(trainerService).getTrainerByUsername(trainerUsername);
        verify(traineeService).updateTrainersList(USERNAME, trainers);
        verify(traineeRestMapper).toAssignedTrainerResponse(trainer);
        verify(traineeRestMapper).toAssignedTrainersUpdateResponse(List.of(trainerResponse));
    }

    @Test
    @DisplayName("Should verify the complete flow of trainer creation from DTO to entity and back")
    void createTrainer_Test() {
        TrainerRequestDTO request = TrainerRequestDTO.builder().build();
        Trainer trainer = Trainer.builder().build();
        Trainer createdTrainer = Trainer.builder().id(TRAINER_ID).build();

        TrainingType trainingType = TrainingType.builder()
                .trainingTypeName("Yoga")
                .build();
        TrainerResponseDTO expected = TrainerResponseDTO.builder()
                .id(TRAINER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .isActive(true)
                .specialization(trainingType)
                .build();

        when(trainerMapper.dtoToEntity(request)).thenReturn(trainer);
        when(trainerService.createTrainer(trainer)).thenReturn(createdTrainer);
        when(trainerMapper.entityToDto(createdTrainer)).thenReturn(expected);

        TrainerResponseDTO actual = facade.createTrainer(request);

        assertEquals(expected, actual);
        assertEquals(USERNAME, actual.getUsername());

        verify(trainerMapper).dtoToEntity(request);
        verify(trainerService).createTrainer(trainer);
        verify(trainerMapper).entityToDto(createdTrainer);
    }

    @Test
    @DisplayName("Should successfully update trainer profile by orchestrating DTO mapping and service calls")
    void updateTrainer_Test() {
        Trainer trainer = Trainer.builder().id(TRAINER_ID).build();

        TrainerResponseDTO expected = TrainerResponseDTO.builder()
                .id(TRAINER_ID)
                .build();

        when(trainerService.getTrainerById(TRAINER_ID)).thenReturn(trainer);
        when(trainerMapper.entityToDto(trainer)).thenReturn(expected);

        TrainerResponseDTO actual = facade.getTrainerById(TRAINER_ID);

        assertEquals(expected, actual);

        verify(trainerService).getTrainerById(TRAINER_ID);
        verify(trainerMapper).entityToDto(trainer);
    }

    @Test
    @DisplayName("Should get trainer by username")
    void getTrainerByUsername_shouldReturnMappedDto() {
        Trainer trainer = Trainer.builder().id(TRAINER_ID).build();

        TrainerResponseDTO expected = TrainerResponseDTO.builder()
                .id(TRAINER_ID)
                .username(USERNAME)
                .build();

        when(trainerService.getTrainerByUsername(USERNAME)).thenReturn(trainer);
        when(trainerMapper.entityToDto(trainer)).thenReturn(expected);

        TrainerResponseDTO actual = facade.getTrainerByUsername(USERNAME);

        assertEquals(expected, actual);

        verify(trainerService).getTrainerByUsername(USERNAME);
        verify(trainerMapper).entityToDto(trainer);
    }

    @Test
    @DisplayName("Should retrieve all trainers from service and map the collection to response DTOs")
    void getAllTrainers_Test() {
        Trainer trainer = Trainer.builder().id(TRAINER_ID).build();

        TrainerResponseDTO response = TrainerResponseDTO.builder()
                .id(TRAINER_ID)
                .username(USERNAME)
                .build();

        when(trainerService.getAllTrainers()).thenReturn(List.of(trainer));
        when(trainerMapper.entityToDto(trainer)).thenReturn(response);

        List<TrainerResponseDTO> actual = facade.getAllTrainers();

        assertEquals(1, actual.size());
        assertEquals(USERNAME, actual.get(0).getUsername());

        verify(trainerService).getAllTrainers();
        verify(trainerMapper).entityToDto(trainer);
    }

    @Test
    @DisplayName("Should update trainer")
    void updateTrainer_shouldMapRequestCallServiceAndMapResponse() {
        TrainerUpdateDTO request = TrainerUpdateDTO.builder().build();
        Trainer trainer = Trainer.builder().id(TRAINER_ID).build();
        Trainer updated = Trainer.builder().id(TRAINER_ID).build();

        TrainerResponseDTO expected = TrainerResponseDTO.builder()
                .id(TRAINER_ID)
                .lastName(LAST_NAME)
                .build();

        when(trainerMapper.dtoToEntity(request)).thenReturn(trainer);
        when(trainerService.updateTrainer(trainer)).thenReturn(updated);
        when(trainerMapper.entityToDto(updated)).thenReturn(expected);

        TrainerResponseDTO actual = facade.updateTrainer(request);

        assertEquals(expected, actual);

        verify(trainerMapper).dtoToEntity(request);
        verify(trainerService).updateTrainer(trainer);
        verify(trainerMapper).entityToDto(updated);
    }

    @Test
    @DisplayName("Should change trainer password")
    void changeTrainerPassword_shouldDelegateToService() {
        facade.changeTrainerPassword(USERNAME, NEW_PASSWORD);

        verify(trainerService).changePassword(USERNAME, NEW_PASSWORD);
    }

    @Test
    @DisplayName("Should activate trainer")
    void activateTrainer_shouldActivateAndMapResponse() {
        Trainer trainer = Trainer.builder().id(TRAINER_ID).build();
        TrainerResponseDTO expected = TrainerResponseDTO.builder()
                .id(TRAINER_ID)
                .isActive(true)
                .build();

        when(trainerService.activateTrainer(USERNAME)).thenReturn(trainer);
        when(trainerMapper.entityToDto(trainer)).thenReturn(expected);

        TrainerResponseDTO actual = facade.activateTrainer(USERNAME);

        assertEquals(expected, actual);

        verify(trainerService).activateTrainer(USERNAME);
        verify(trainerMapper).entityToDto(trainer);
    }

    @Test
    @DisplayName("Should deactivate trainer")
    void deactivateTrainer_shouldDeactivateAndMapResponse() {
        Trainer trainer = Trainer.builder().id(TRAINER_ID).build();

        TrainerResponseDTO expected = TrainerResponseDTO.builder()
                .id(TRAINER_ID)
                .isActive(false)
                .build();

        when(trainerService.deactivateTrainer(USERNAME)).thenReturn(trainer);
        when(trainerMapper.entityToDto(trainer)).thenReturn(expected);

        TrainerResponseDTO actual = facade.deactivateTrainer(USERNAME);

        assertEquals(expected, actual);

        verify(trainerService).deactivateTrainer(USERNAME);
        verify(trainerMapper).entityToDto(trainer);
    }

    @Test
    @DisplayName("Should get trainer trainings")
    void getTrainerTrainings_shouldReturnMappedTrainingList() {
        LocalDate fromDate = LocalDate.of(2026, 2, 1);
        LocalDate toDate = LocalDate.of(2026, 2, 28);
        String traineeName = "Ivan Trainee";

        Training training = Training.builder()
                .id(TRAINING_ID)
                .trainingName("Boxing")
                .build();
        TrainingResponseDTO response = TrainingResponseDTO.builder()
                .trainingName("Boxing")
                .build();

        when(trainerService.getTrainerTrainings(USERNAME, fromDate, toDate, traineeName)).thenReturn(List.of(training));

        when(trainingMapper.entityToDto(training)).thenReturn(response);

        List<TrainingResponseDTO> actual = facade.getTrainerTrainings(USERNAME, fromDate, toDate, traineeName);

        assertEquals(1, actual.size());
        assertEquals("Boxing", actual.get(0).getTrainingName());

        verify(trainerService).getTrainerTrainings(USERNAME, fromDate, toDate, traineeName);
        verify(trainingMapper).entityToDto(training);
    }

    @Test
    @DisplayName("Should create training")
    void createTraining_shouldResolveTraineeAndTrainerThenMapAndCreateTraining() {
        TrainingType requestTrainingType = TrainingType.builder()
                .trainingTypeName("Yoga")
                .build();
        TrainingRequestDTO request = TrainingRequestDTO.builder()
                .traineeId(TRAINEE_ID)
                .trainerId(TRAINER_ID)
                .trainingName("Morning Yoga")
                .trainingType(requestTrainingType)
                .trainingDuration(60)
                .trainingDate(LocalDate.of(2026, 4, 10))
                .build();
        Trainee trainee = Trainee.builder()
                .id(TRAINEE_ID)
                .build();
        Trainer trainer = Trainer.builder()
                .id(TRAINER_ID)
                .build();
        Training training = Training.builder()
                .trainingName("Morning Yoga")
                .build();
        Training createdTraining = Training.builder()
                .id(TRAINING_ID)
                .trainingName("Morning Yoga")
                .build();
        TrainingResponseDTO expected = TrainingResponseDTO.builder()
                .traineeId(TRAINEE_ID)
                .trainerId(TRAINER_ID)
                .trainingName("Morning Yoga")
                .trainingDuration(60)
                .trainingDate(LocalDate.of(2026, 4, 10))
                .build();

        when(traineeService.getTraineeById(TRAINEE_ID)).thenReturn(trainee);
        when(trainerService.getTrainerById(TRAINER_ID)).thenReturn(trainer);

        when(trainingMapper.dtoToEntity(eq(request), eq(trainee), eq(trainer), any(TrainingType.class))).thenReturn(training);

        when(trainingService.createTraining(training)).thenReturn(createdTraining);
        when(trainingMapper.entityToDto(createdTraining)).thenReturn(expected);

        TrainingResponseDTO actual = facade.createTraining(request);

        assertEquals(expected, actual);
        assertEquals("Morning Yoga", actual.getTrainingName());

        verify(traineeService).getTraineeById(TRAINEE_ID);
        verify(trainerService).getTrainerById(TRAINER_ID);
        verify(trainingMapper).dtoToEntity(eq(request), eq(trainee), eq(trainer), any(TrainingType.class));
        verify(trainingService).createTraining(training);
        verify(trainingMapper).entityToDto(createdTraining);
    }

    @Test
    @DisplayName("Should get training by ID")
    void getTrainingById_shouldReturnMappedDto() {
        Training training = Training.builder()
                .id(TRAINING_ID)
                .build();
        TrainingResponseDTO expected = TrainingResponseDTO.builder()
                .trainingName("Morning Yoga")
                .build();

        when(trainingService.getTrainingById(TRAINING_ID)).thenReturn(training);
        when(trainingMapper.entityToDto(training)).thenReturn(expected);

        TrainingResponseDTO actual = facade.getTrainingById(TRAINING_ID);

        assertEquals(expected, actual);

        verify(trainingService).getTrainingById(TRAINING_ID);
        verify(trainingMapper).entityToDto(training);
    }

    @Test
    @DisplayName("Should get all trainings")
    void getAllTrainings_shouldReturnMappedDtoList() {
        Training training = Training.builder()
                .id(TRAINING_ID)
                .trainingName("Boxing")
                .build();
        TrainingResponseDTO response = TrainingResponseDTO.builder()
                .trainingName("Boxing")
                .build();

        when(trainingService.getAllTrainings()).thenReturn(List.of(training));
        when(trainingMapper.entityToDto(training)).thenReturn(response);

        List<TrainingResponseDTO> actual = facade.getAllTrainings();

        assertEquals(1, actual.size());
        assertEquals("Boxing", actual.get(0).getTrainingName());
        verify(trainingService).getAllTrainings();
        verify(trainingMapper).entityToDto(training);
    }

    @Test
    void changePasswordShouldCallUserService() {
        LoginChangeRequest request = new LoginChangeRequest()
                .username("test.user")
                .oldPassword("old-password")
                .newPassword("new-password");

        facade.changePassword(request);

        verify(userService).changePassword(request);
    }
}