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
import com.gym.crm.application.model.Trainee;
import com.gym.crm.application.model.Trainer;
import com.gym.crm.application.model.Training;
import com.gym.crm.application.model.TrainingType;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GymAppFacadeTest {

    private final String USER_FIRST_NAME = "Ivan";
    private final String USER_LAST_NAME = "Ivanov";
    private final String USERNAME = USER_FIRST_NAME + '.' + USER_LAST_NAME;
    private final Long ENTITY_ID = 1L;

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
    @DisplayName("Verify that facade calls trainee service and uses mappers for create operation")
    void createTrainee_Test() {
        TraineeRequestDTO request = TraineeRequestDTO.builder().build();
        Trainee trainee = new Trainee();
        Trainee savedTrainee = new Trainee();

        TraineeResponseDTO expected = TraineeResponseDTO.builder()
                .id(ENTITY_ID)
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .username(USERNAME)
                .isActive(true)
                .dateOfBirth(LocalDate.of(2000, 5, 10))
                .address("Kyiv")
                .build();

        when(traineeMapper.dtoToEntity(request)).thenReturn(trainee);
        when(traineeService.createTrainee(trainee)).thenReturn(savedTrainee);
        when(traineeMapper.entityToDto(savedTrainee)).thenReturn(expected);

        TraineeResponseDTO actual = facade.createTrainee(request);

        assertEquals(expected, actual);
        assertEquals(USERNAME, actual.getUsername());
        verify(traineeService).createTrainee(trainee);
    }

    @Test
    @DisplayName("Should return trainee DTO when a valid ID is provided to the facade")
    void getTraineeById_Test() {
        Trainee trainee = new Trainee();
        TraineeResponseDTO expected = TraineeResponseDTO.builder().id(ENTITY_ID).firstName(USER_FIRST_NAME).build();

        when(traineeService.getTraineeById(ENTITY_ID)).thenReturn(trainee);
        when(traineeMapper.entityToDto(trainee)).thenReturn(expected);

        assertEquals(expected, facade.getTraineeById(ENTITY_ID));
    }

    @Test
    @DisplayName("Should retrieve all trainees from service and map them to a list of response DTOs")
    void getAllTrainees_Test() {
        Trainee trainee = new Trainee();
        TraineeResponseDTO response = TraineeResponseDTO.builder().username(USERNAME).build();

        when(traineeService.getAllTrainees()).thenReturn(List.of(trainee));
        when(traineeMapper.entityToDto(trainee)).thenReturn(response);

        List<TraineeResponseDTO> actual = facade.getAllTrainees();

        assertEquals(1, actual.size());
        assertEquals(USERNAME, actual.get(0).getUsername());
    }

    @Test
    @DisplayName("Should successfully update trainee by mapping request DTO to entity and returning response DTO")
    void updateTrainee_Test() {
        TraineeRequestDTO request = TraineeRequestDTO.builder().build();
        Trainee trainee = new Trainee();
        Trainee updated = new Trainee();
        TraineeResponseDTO expected = TraineeResponseDTO.builder().id(ENTITY_ID).isActive(false).build();

        when(traineeMapper.dtoToEntity(request)).thenReturn(trainee);
        when(traineeService.updateTrainee(trainee)).thenReturn(updated);
        when(traineeMapper.entityToDto(updated)).thenReturn(expected);

        assertEquals(expected, facade.updateTrainee(request));
    }

    @Test
    @DisplayName("Should successfully delegate trainee deletion to the service layer using the provided ID")
    void deleteTrainee_Test() {
        facade.deleteTrainee(ENTITY_ID);

        verify(traineeService, times(1)).deleteTrainee(ENTITY_ID);
    }

    @Test
    @DisplayName("Should verify the complete flow of trainer creation from DTO to entity and back")
    void createTrainer_Test() {
        TrainerRequestDTO request = TrainerRequestDTO.builder().build();
        Trainer trainer = new Trainer();
        Trainer saved = new Trainer();

        TrainerResponseDTO expected = TrainerResponseDTO.builder()
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .username(USERNAME)
                .isActive(true)
                .specialization(new TrainingType())
                .build();

        when(trainerMapper.dtoToEntity(request)).thenReturn(trainer);
        when(trainerService.createTrainer(trainer)).thenReturn(saved);
        when(trainerMapper.entityToDto(saved)).thenReturn(expected);

        TrainerResponseDTO actual = facade.createTrainer(request);

        assertEquals(expected, actual);
        assertEquals(USERNAME, actual.getUsername());
    }

    @Test
    @DisplayName("Should successfully update trainer profile by orchestrating DTO mapping and service calls")
    void updateTrainer_Test() {
        TrainerRequestDTO request = TrainerRequestDTO.builder().build();
        Trainer trainer = new Trainer();
        Trainer updated = new Trainer();
        TrainerResponseDTO expected = TrainerResponseDTO.builder().lastName(USER_LAST_NAME).build();

        when(trainerMapper.dtoToEntity(request)).thenReturn(trainer);
        when(trainerService.updateTrainer(trainer)).thenReturn(updated);
        when(trainerMapper.entityToDto(updated)).thenReturn(expected);

        assertEquals(expected, facade.updateTrainer(request));
    }

    @Test
    @DisplayName("Should successfully retrieve trainer by ID and convert entity to response DTO")
    void getTrainerById_Test() {
        TrainerResponseDTO expected = TrainerResponseDTO.builder().id(ENTITY_ID).build();
        Trainer trainer = new Trainer();

        when(trainerService.getTrainerById(ENTITY_ID)).thenReturn(trainer);
        when(trainerMapper.entityToDto(trainer)).thenReturn(expected);

        assertEquals(expected, facade.getTrainerById(ENTITY_ID));
    }

    @Test
    @DisplayName("Should retrieve all trainers from service and map the collection to response DTOs")
    void getAllTrainers_Test() {
        Trainer trainer = new Trainer();
        TrainerResponseDTO response = TrainerResponseDTO.builder().username(USERNAME).build();

        when(trainerService.getAllTrainers()).thenReturn(List.of(trainer));
        when(trainerMapper.entityToDto(trainer)).thenReturn(response);

        List<TrainerResponseDTO> actual = facade.getAllTrainers();

        assertEquals(1, actual.size());
        assertEquals(USERNAME, actual.get(0).getUsername());
    }

    @Test
    @DisplayName("Verify the complete flow of mapping and persisting a new training session through the facade")
    void createTraining_Test() {
        TrainingRequestDTO request = TrainingRequestDTO.builder().build();
        Training training = new Training();
        Training saved = new Training();

        TrainingResponseDTO expected = TrainingResponseDTO.builder()
                .traineeId(ENTITY_ID)
                .trainerId(ENTITY_ID)
                .trainingName("Morning Yoga")
                .trainingDuration(60)
                .trainingDate(LocalDate.now())
                .build();

        when(trainingMapper.dtoToEntity(request)).thenReturn(training);
        when(trainingService.createTraining(training)).thenReturn(saved);
        when(trainingMapper.entityToDto(saved)).thenReturn(expected);

        TrainingResponseDTO actual = facade.createTraining(request);

        assertEquals(expected, actual);
        assertEquals("Morning Yoga", actual.getTrainingName());
        assertEquals(60, actual.getTrainingDuration());
    }

    @Test
    @DisplayName("Should successfully retrieve training session by ID and return its corresponding response DTO")
    void getTrainingById_Test() {
        Training training = new Training();
        TrainingResponseDTO expected = TrainingResponseDTO.builder().build();

        when(trainingService.getTrainingById(ENTITY_ID)).thenReturn(training);
        when(trainingMapper.entityToDto(training)).thenReturn(expected);

        assertEquals(expected, facade.getTrainingById(ENTITY_ID));
    }

    @Test
    @DisplayName("Should retrieve all training sessions and correctly map them to a list of response DTOs")
    void getAllTrainings_Test() {
        Training training = new Training();
        TrainingResponseDTO response = TrainingResponseDTO.builder().trainingName("Boxing").build();

        when(trainingService.getAllTrainings()).thenReturn(List.of(training));
        when(trainingMapper.entityToDto(training)).thenReturn(response);

        List<TrainingResponseDTO> actual = facade.getAllTrainings();

        assertEquals(1, actual.size());
        assertEquals("Boxing", actual.get(0).getTrainingName());
    }
}