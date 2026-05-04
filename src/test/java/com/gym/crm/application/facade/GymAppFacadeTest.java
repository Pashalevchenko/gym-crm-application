package com.gym.crm.application.facade;

import com.gym.crm.application.dto.mapper.TraineeMapper;
import com.gym.crm.application.dto.mapper.TrainerMapper;
import com.gym.crm.application.dto.mapper.TrainingMapper;
import com.gym.crm.application.dto.request.TraineeRequestDTO;
import com.gym.crm.application.dto.request.TrainerRequestDTO;
import com.gym.crm.application.dto.request.TrainingRequestDTO;
import com.gym.crm.application.dto.response.TraineeResponseDTO;
import com.gym.crm.application.dto.response.TrainerResponseDTO;
import com.gym.crm.application.dto.response.TrainingResponseDTO;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.Training;
import com.gym.crm.application.entity.TrainingType;
import com.gym.crm.application.service.TraineeService;
import com.gym.crm.application.service.TrainerService;
import com.gym.crm.application.service.TrainingService;
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

@ExtendWith(MockitoExtension.class)
class GymAppFacadeTest {

    private static final String FIRST_NAME = "Ivan";
    private static final String LAST_NAME = "Ivanov";
    private static final String USERNAME = "ivan.ivanov";
    private static final String PASSWORD = "12345";
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
    private TrainerMapper trainerMapper;

    @Mock
    private TrainingMapper trainingMapper;

    @InjectMocks
    private GymAppFacade facade;

    @Test
    @DisplayName("Should create trainee using mapper and service")
    void createTrainee_shouldMapRequestCallServiceAndMapResponse() {
        TraineeRequestDTO request = TraineeRequestDTO.builder().build();
        Trainee trainee = Trainee.builder().build();
        Trainee createdTrainee = Trainee.builder().id(TRAINEE_ID).build();

        TraineeResponseDTO expected = TraineeResponseDTO.builder()
                .id(TRAINEE_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .isActive(true)
                .dateOfBirth(LocalDate.of(2000, 5, 10))
                .address("Kyiv")
                .build();

        when(traineeMapper.dtoToEntity(request)).thenReturn(trainee);
        when(traineeService.createTrainee(trainee)).thenReturn(createdTrainee);
        when(traineeMapper.entityToDto(createdTrainee)).thenReturn(expected);

        TraineeResponseDTO actual = facade.createTrainee(request);

        assertEquals(expected, actual);
        assertEquals(USERNAME, actual.getUsername());

        verify(traineeMapper).dtoToEntity(request);
        verify(traineeService).createTrainee(trainee);
        verify(traineeMapper).entityToDto(createdTrainee);
    }

    @Test
    @DisplayName("Should get trainee by ID")
    void getTraineeById_shouldReturnMappedDto() {
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
        TraineeResponseDTO expected = TraineeResponseDTO.builder()
                .id(TRAINEE_ID)
                .username(USERNAME)
                .build();

        when(traineeService.getTraineeByUsername(USERNAME)).thenReturn(trainee);
        when(traineeMapper.entityToDto(trainee)).thenReturn(expected);

        TraineeResponseDTO actual = facade.getTraineeByUsername(USERNAME);

        assertEquals(expected, actual);

        verify(traineeService).getTraineeByUsername(USERNAME);
        verify(traineeMapper).entityToDto(trainee);
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
    @DisplayName("Should update trainee")
    void updateTrainee_shouldMapRequestCallServiceAndMapResponse() {
        TraineeRequestDTO request = TraineeRequestDTO.builder().build();
        Trainee trainee = Trainee.builder().id(TRAINEE_ID).build();
        Trainee updated = Trainee.builder().id(TRAINEE_ID).build();

        TraineeResponseDTO expected = TraineeResponseDTO.builder()
                .id(TRAINEE_ID)
                .isActive(false)
                .build();

        when(traineeMapper.dtoToEntity(request)).thenReturn(trainee);
        when(traineeService.updateTrainee(trainee)).thenReturn(updated);
        when(traineeMapper.entityToDto(updated)).thenReturn(expected);

        TraineeResponseDTO actual = facade.updateTrainee(request);

        assertEquals(expected, actual);

        verify(traineeMapper).dtoToEntity(request);
        verify(traineeService).updateTrainee(trainee);
        verify(traineeMapper).entityToDto(updated);
    }

    @Test
    @DisplayName("Should check trainee password")
    void isTraineePasswordCorrect_shouldDelegateToService() {
        when(traineeService.isPasswordCorrect(USERNAME, PASSWORD)).thenReturn(true);

        boolean actual = facade.isTraineePasswordCorrect(USERNAME, PASSWORD);

        assertEquals(true, actual);

        verify(traineeService).isPasswordCorrect(USERNAME, PASSWORD);
    }

    @Test
    @DisplayName("Should change trainee password")
    void changeTraineePassword_shouldDelegateToService() {
        facade.changeTraineePassword(USERNAME, NEW_PASSWORD);

        verify(traineeService).changePassword(USERNAME, NEW_PASSWORD);
    }

    @Test
    @DisplayName("Should activate trainee")
    void activateTrainee_shouldActivateAndMapResponse() {
        Trainee trainee = Trainee.builder().id(TRAINEE_ID).build();
        TraineeResponseDTO expected = TraineeResponseDTO.builder()
                .id(TRAINEE_ID)
                .isActive(true)
                .build();

        when(traineeService.activateTrainee(USERNAME)).thenReturn(trainee);
        when(traineeMapper.entityToDto(trainee)).thenReturn(expected);

        TraineeResponseDTO actual = facade.activateTrainee(USERNAME);

        assertEquals(expected, actual);

        verify(traineeService).activateTrainee(USERNAME);
        verify(traineeMapper).entityToDto(trainee);
    }

    @Test
    @DisplayName("Should deactivate trainee")
    void deactivateTrainee_shouldDeactivateAndMapResponse() {
        Trainee trainee = Trainee.builder().id(TRAINEE_ID).build();
        TraineeResponseDTO expected = TraineeResponseDTO.builder()
                .id(TRAINEE_ID)
                .isActive(false)
                .build();

        when(traineeService.deactivateTrainee(USERNAME)).thenReturn(trainee);
        when(traineeMapper.entityToDto(trainee)).thenReturn(expected);

        TraineeResponseDTO actual = facade.deactivateTrainee(USERNAME);

        assertEquals(expected, actual);

        verify(traineeService).deactivateTrainee(USERNAME);
        verify(traineeMapper).entityToDto(trainee);
    }

    @Test
    @DisplayName("Should delete trainee by ID")
    void deleteTrainee_shouldDelegateToService() {
        facade.deleteTrainee(TRAINEE_ID);

        verify(traineeService).deleteTrainee(TRAINEE_ID);
    }

    @Test
    @DisplayName("Should delete trainee by username")
    void deleteTraineeByUsername_shouldDelegateToService() {
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

        TrainingResponseDTO response = TrainingResponseDTO.builder()
                .trainingName("Morning Yoga")
                .build();

        when(traineeService.getTraineeTrainings(
                USERNAME,
                fromDate,
                toDate,
                trainerName,
                trainingTypeName
        )).thenReturn(List.of(training));

        when(trainingMapper.entityToDto(training)).thenReturn(response);

        List<TrainingResponseDTO> actual = facade.getTraineeTrainings(
                USERNAME,
                fromDate,
                toDate,
                trainerName,
                trainingTypeName
        );

        assertEquals(1, actual.size());
        assertEquals("Morning Yoga", actual.get(0).getTrainingName());

        verify(traineeService).getTraineeTrainings(
                USERNAME,
                fromDate,
                toDate,
                trainerName,
                trainingTypeName
        );
        verify(trainingMapper).entityToDto(training);
    }

    @Test
    @DisplayName("Should get not assigned trainers")
    void getNotAssignedTrainers_shouldReturnMappedTrainerList() {
        Trainer trainer = Trainer.builder().id(TRAINER_ID).build();

        TrainerResponseDTO response = TrainerResponseDTO.builder()
                .id(TRAINER_ID)
                .username("trainer.username")
                .build();

        when(traineeService.getNotAssignedTrainers(USERNAME)).thenReturn(List.of(trainer));
        when(trainerMapper.entityToDto(trainer)).thenReturn(response);

        List<TrainerResponseDTO> actual = facade.getNotAssignedTrainers(USERNAME);

        assertEquals(1, actual.size());
        assertEquals("trainer.username", actual.get(0).getUsername());

        verify(traineeService).getNotAssignedTrainers(USERNAME);
        verify(trainerMapper).entityToDto(trainer);
    }

    @Test
    @DisplayName("Should update trainee trainers list")
    void updateTraineeTrainersList_shouldDelegateToServiceAndMapResponse() {
        Set<Trainer> trainers = Set.of(Trainer.builder()
                .id(TRAINER_ID)
                .build());

        Trainee trainee = Trainee.builder()
                .id(TRAINEE_ID)
                .build();

        TraineeResponseDTO expected = TraineeResponseDTO.builder()
                .id(TRAINEE_ID)
                .build();

        when(traineeService.updateTrainersList(USERNAME, trainers)).thenReturn(trainee);
        when(traineeMapper.entityToDto(trainee)).thenReturn(expected);

        TraineeResponseDTO actual = facade.updateTraineeTrainersList(USERNAME, trainers);

        assertEquals(expected, actual);

        verify(traineeService).updateTrainersList(USERNAME, trainers);
        verify(traineeMapper).entityToDto(trainee);
    }

    @Test
    @DisplayName("Should create trainer using mapper and service")
    void createTrainer_shouldMapRequestCallServiceAndMapResponse() {
        TrainerRequestDTO request = TrainerRequestDTO.builder().build();
        Trainer trainer = Trainer.builder().build();
        Trainer createdTrainer = Trainer.builder().id(TRAINER_ID).build();

        TrainerResponseDTO expected = TrainerResponseDTO.builder()
                .id(TRAINER_ID)
                .firstName(FIRST_NAME)
                .lastName(LAST_NAME)
                .username(USERNAME)
                .isActive(true)
                .specialization(TrainingType.builder()
                        .trainingTypeName("Yoga")
                        .build())
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
    @DisplayName("Should get trainer by ID")
    void getTrainerById_shouldReturnMappedDto() {
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
    @DisplayName("Should get all trainers")
    void getAllTrainers_shouldReturnMappedDtoList() {
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
        TrainerRequestDTO request = TrainerRequestDTO.builder().build();
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
    @DisplayName("Should check trainer password")
    void isTrainerPasswordCorrect_shouldDelegateToService() {
        when(trainerService.isPasswordCorrect(USERNAME, PASSWORD)).thenReturn(true);

        boolean actual = facade.isTrainerPasswordCorrect(USERNAME, PASSWORD);

        assertEquals(true, actual);

        verify(trainerService).isPasswordCorrect(USERNAME, PASSWORD);
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

        when(trainerService.getTrainerTrainings(
                USERNAME,
                fromDate,
                toDate,
                traineeName
        )).thenReturn(List.of(training));

        when(trainingMapper.entityToDto(training)).thenReturn(response);

        List<TrainingResponseDTO> actual = facade.getTrainerTrainings(
                USERNAME,
                fromDate,
                toDate,
                traineeName
        );

        assertEquals(1, actual.size());
        assertEquals("Boxing", actual.get(0).getTrainingName());

        verify(trainerService).getTrainerTrainings(
                USERNAME,
                fromDate,
                toDate,
                traineeName
        );
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
                .trainingType(requestTrainingType)
                .build();

        Trainee trainee = Trainee.builder()
                .id(TRAINEE_ID)
                .build();

        Trainer trainer = Trainer.builder()
                .id(TRAINER_ID)
                .build();

        TrainingType trainingType = TrainingType.builder()
                .trainingTypeName("Yoga")
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
        when(trainingMapper.dtoToEntity(request, trainee, trainer, trainingType)).thenReturn(training);
        when(trainingService.createTraining(training)).thenReturn(createdTraining);
        when(trainingMapper.entityToDto(createdTraining)).thenReturn(expected);

        TrainingResponseDTO actual = facade.createTraining(request);

        assertEquals(expected, actual);
        assertEquals("Morning Yoga", actual.getTrainingName());

        verify(traineeService).getTraineeById(TRAINEE_ID);
        verify(trainerService).getTrainerById(TRAINER_ID);
        verify(trainingMapper).dtoToEntity(request, trainee, trainer, trainingType);
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
}