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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GymAppFacadeTest {

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

    private final Long ENTITY_ID = 1L;

    @InjectMocks
    private GymAppFacade facade;

    private final String USER_FIRST_NAME = "Ivan";
    private final String USER_LAST_NAME = "Ivanov";
    private final String USERNAME = USER_FIRST_NAME + '.' + USER_LAST_NAME;

    @Test
    void createTrainee_Test() {
        TraineeRequestDTO request = TraineeRequestDTO.builder().build();
        Trainee trainee = new Trainee();
        Trainee savedTrainee = new Trainee();

        TraineeResponseDTO expectedResponse = TraineeResponseDTO.builder()
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
        when(traineeMapper.entityToDto(savedTrainee)).thenReturn(expectedResponse);

        TraineeResponseDTO result = facade.createTrainee(request);

        assertEquals(expectedResponse, result);
        assertEquals(USERNAME, result.getUsername());
        verify(traineeService).createTrainee(trainee);
    }

    @Test
    void getTraineeById_Test() {
        Trainee trainee = new Trainee();
        TraineeResponseDTO response = TraineeResponseDTO.builder().id(ENTITY_ID).firstName(USER_FIRST_NAME).build();

        when(traineeService.getTraineeById(ENTITY_ID)).thenReturn(trainee);
        when(traineeMapper.entityToDto(trainee)).thenReturn(response);

        assertEquals(response, facade.getTraineeById(ENTITY_ID));
    }

    @Test
    void getAllTrainees_Test() {
        Trainee trainee = new Trainee();
        TraineeResponseDTO response = TraineeResponseDTO.builder().username(USERNAME).build();

        when(traineeService.getAllTrainees()).thenReturn(List.of(trainee));
        when(traineeMapper.entityToDto(trainee)).thenReturn(response);

        List<TraineeResponseDTO> result = facade.getAllTrainees();

        assertEquals(1, result.size());
        assertEquals(USERNAME, result.get(0).getUsername());
    }

    @Test
    void updateTrainee_Test() {
        TraineeRequestDTO request = TraineeRequestDTO.builder().build();
        Trainee trainee = new Trainee();
        Trainee updated = new Trainee();
        TraineeResponseDTO response = TraineeResponseDTO.builder().id(ENTITY_ID).isActive(false).build();

        when(traineeMapper.dtoToEntity(request)).thenReturn(trainee);
        when(traineeService.updateTrainee(trainee)).thenReturn(updated);
        when(traineeMapper.entityToDto(updated)).thenReturn(response);

        assertEquals(response, facade.updateTrainee(request));
    }

    @Test
    void deleteTrainee_Test() {
        facade.deleteTrainee(ENTITY_ID);

        verify(traineeService, times(1)).deleteTrainee(ENTITY_ID);
    }

    @Test
    void createTrainer_Test() {
        TrainerRequestDTO request = TrainerRequestDTO.builder().build();
        Trainer trainer = new Trainer();
        Trainer saved = new Trainer();

        TrainerResponseDTO expectedResponse = TrainerResponseDTO.builder()
                .firstName(USER_FIRST_NAME)
                .lastName(USER_LAST_NAME)
                .username(USERNAME)
                .isActive(true)
                .specialization(new TrainingType())
                .build();

        when(trainerMapper.dtoToEntity(request)).thenReturn(trainer);
        when(trainerService.createTrainer(trainer)).thenReturn(saved);
        when(trainerMapper.entityToDto(saved)).thenReturn(expectedResponse);

        TrainerResponseDTO result = facade.createTrainer(request);

        assertEquals(expectedResponse, result);
        assertEquals(USERNAME, result.getUsername());
    }

    @Test
    void updateTrainer_Test() {
        TrainerRequestDTO request = TrainerRequestDTO.builder().build();
        Trainer trainer = new Trainer();
        Trainer updated = new Trainer();
        TrainerResponseDTO response = TrainerResponseDTO.builder().lastName(USER_LAST_NAME).build();

        when(trainerMapper.dtoToEntity(request)).thenReturn(trainer);
        when(trainerService.updateTrainer(trainer)).thenReturn(updated);
        when(trainerMapper.entityToDto(updated)).thenReturn(response);

        assertEquals(response, facade.updateTrainer(request));
    }

    @Test
    void getTrainerById_Test() {
        TrainerResponseDTO response = TrainerResponseDTO.builder().id(ENTITY_ID).build();
        Trainer trainer = new Trainer();

        when(trainerService.getTrainerById(ENTITY_ID)).thenReturn(trainer);
        when(trainerMapper.entityToDto(trainer)).thenReturn(response);

        assertEquals(response, facade.getTrainerById(ENTITY_ID));
    }

    @Test
    void getAllTrainers_Test() {
        Trainer trainer = new Trainer();
        TrainerResponseDTO response = TrainerResponseDTO.builder().username(USERNAME).build();

        when(trainerService.getAllTrainers()).thenReturn(List.of(trainer));
        when(trainerMapper.entityToDto(trainer)).thenReturn(response);

        List<TrainerResponseDTO> result = facade.getAllTrainers();

        assertEquals(1, result.size());
        assertEquals(USERNAME, result.get(0).getUsername());
    }

    @Test
    void createTraining_Test() {
        TrainingRequestDTO request = TrainingRequestDTO.builder().build();
        Training training = new Training();
        Training saved = new Training();

        TrainingResponseDTO expectedResponse = TrainingResponseDTO.builder()
                .traineeId(ENTITY_ID)
                .trainerId(ENTITY_ID)
                .trainingName("Morning Yoga")
                .trainingDuration(60)
                .trainingDate(LocalDate.now())
                .build();

        when(trainingMapper.dtoToEntity(request)).thenReturn(training);
        when(trainingService.createTraining(training)).thenReturn(saved);
        when(trainingMapper.entityToDto(saved)).thenReturn(expectedResponse);

        TrainingResponseDTO result = facade.createTraining(request);

        assertEquals(expectedResponse, result);
        assertEquals("Morning Yoga", result.getTrainingName());
        assertEquals(60, result.getTrainingDuration());
    }

    @Test
    void getTrainingById_Test() {
        Training training = new Training();
        TrainingResponseDTO response = TrainingResponseDTO.builder().build();

        when(trainingService.getTrainingById(ENTITY_ID)).thenReturn(training);
        when(trainingMapper.entityToDto(training)).thenReturn(response);

        assertEquals(response, facade.getTrainingById(ENTITY_ID));
    }

    @Test
    void getAllTrainings_Test() {
        Training training = new Training();
        TrainingResponseDTO response = TrainingResponseDTO.builder().trainingName("Boxing").build();

        when(trainingService.getAllTrainings()).thenReturn(List.of(training));
        when(trainingMapper.entityToDto(training)).thenReturn(response);

        List<TrainingResponseDTO> result = facade.getAllTrainings();

        assertEquals(1, result.size());
        assertEquals("Boxing", result.get(0).getTrainingName());
    }
}