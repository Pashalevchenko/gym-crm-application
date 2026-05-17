package com.gym.crm.application.facade;

import com.gym.crm.application.aspect.annotation.Authenticated;
import com.gym.crm.application.aspect.annotation.Transactional;
import com.gym.crm.application.context.SecurityContextHolder;
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
import com.gym.crm.application.service.UserService;
import com.gym.crm.application.service.common.AuthenticationService;
import com.gym.crm.application.service.TraineeService;
import com.gym.crm.application.service.TrainerService;
import com.gym.crm.application.service.TrainingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GymAppFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final UserService userService;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;
    private final AuthenticationService authService;
    private final TraineeRestMapper traineeRestMapper;

    @Transactional
    public void login(String username, String password) {
        authService.authenticate(username, password);
        SecurityContextHolder.setContext(username);
    }

    public void logout() {
        SecurityContextHolder.clear();
    }

    public TraineeCreateResponse createTrainee(TraineeCreateRequest request) {
        Trainee trainee = traineeRestMapper.toEntity(request);
        Trainee created = traineeService.createTrainee(trainee);

        return traineeRestMapper.toCreateResponse(created);
    }

    @Authenticated
    public TraineeResponseDTO getTraineeById(Long id) {
        return traineeMapper.entityToDto(traineeService.getTraineeById(id));
    }

    @Authenticated
    @Transactional
    public TraineeGetResponse getTraineeByUsername(String username) {
        Trainee trainee = traineeService.getTraineeByUsername(username);

        return traineeRestMapper.toGetResponse(trainee);
    }

    @Authenticated
    public List<TraineeResponseDTO> getAllTrainees() {
        return traineeService.getAllTrainees().stream()
                .map(traineeMapper::entityToDto)
                .toList();
    }

    @Authenticated
    public TraineeUpdateResponse updateTrainee(TraineeUpdateRequest request, String username) {
        TraineeUpdateDTO dto = traineeRestMapper.toUpdateDto(username, request);
        Trainee trainee = traineeMapper.dtoToEntity(dto);
        Trainee updated = traineeService.updateTrainee(trainee);

        return traineeRestMapper.toUpdateResponse(updated);
    }

    @Authenticated
    public void changeTraineePassword(String username, String newPassword) {
        traineeService.changePassword(username, newPassword);
    }

    @Authenticated
    public void changeActiveStatus(String username, ActivationStatusRequest request) {
        traineeService.changeActiveStatus(username, request.getIsActive());
    }

    @Authenticated
    public void deleteTrainee(Long id) {
        traineeService.deleteTrainee(id);
    }

    @Authenticated
    public void deleteTraineeByUsername(String username) {
        traineeService.deleteTraineeByUsername(username);
    }

    @Authenticated
    public List<GetTraineeTrainingResponse> getTraineeTrainings(String username, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingTypeName) {
        List<Training> trainings = traineeService.getTraineeTrainings(username, fromDate, toDate, trainerName, trainingTypeName);

        return traineeRestMapper.toTrainingResponses(trainings);
    }

    @Authenticated
    public List<AssignedTrainerResponse> getNotAssignedTrainers(String traineeUsername) {
        List<Trainer> trainers = traineeService.getNotAssignedTrainers(traineeUsername);

        return traineeRestMapper.toAssignedTrainerResponses(trainers);
    }

    @Authenticated
    public TraineeAssignedTrainersUpdateResponse updateTraineeTrainersList(String traineeUsername, TraineeAssignedTrainersUpdateRequest request) {
        Set<Trainer> trainers = request.getTrainerUsernames().stream()
                .map(trainerService::getTrainerByUsername)
                .collect(Collectors.toSet());
        Trainee updated = traineeService.updateTrainersList(traineeUsername, trainers);
        List<AssignedTrainerResponse> trainerResponses = updated.getTrainers().stream()
                .map(traineeRestMapper::toAssignedTrainerResponse)
                .toList();

        return traineeRestMapper.toAssignedTrainersUpdateResponse(trainerResponses);
    }

    public TrainerResponseDTO createTrainer(@Valid TrainerRequestDTO request) {
        Trainer trainer = trainerMapper.dtoToEntity(request);

        return trainerMapper.entityToDto(trainerService.createTrainer(trainer));
    }

    @Authenticated
    public TrainerResponseDTO getTrainerById(Long id) {
        return trainerMapper.entityToDto(trainerService.getTrainerById(id));
    }

    @Authenticated
    public TrainerResponseDTO getTrainerByUsername(String username) {
        return trainerMapper.entityToDto(trainerService.getTrainerByUsername(username));
    }

    @Authenticated
    public List<TrainerResponseDTO> getAllTrainers() {
        return trainerService.getAllTrainers().stream()
                .map(trainerMapper::entityToDto)
                .toList();
    }

    @Authenticated
    public TrainerResponseDTO updateTrainer(@Valid TrainerUpdateDTO request) {
        Trainer trainer = trainerMapper.dtoToEntity(request);

        return trainerMapper.entityToDto(trainerService.updateTrainer(trainer));
    }

    @Authenticated
    public void changeTrainerPassword(String username, String newPassword) {
        trainerService.changePassword(username, newPassword);
    }

    @Authenticated
    public TrainerResponseDTO activateTrainer(String username) {
        return trainerMapper.entityToDto(trainerService.activateTrainer(username));
    }

    @Authenticated
    public TrainerResponseDTO deactivateTrainer(String username) {
        return trainerMapper.entityToDto(trainerService.deactivateTrainer(username));
    }

    @Authenticated
    public List<TrainingResponseDTO> getTrainerTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName) {
        return trainerService.getTrainerTrainings(username, fromDate, toDate, traineeName).stream()
                .map(trainingMapper::entityToDto)
                .toList();
    }

    @Authenticated
    public TrainingResponseDTO createTraining(@Valid TrainingRequestDTO request) {
        Trainee trainee = traineeService.getTraineeById(request.getTraineeId());
        Trainer trainer = trainerService.getTrainerById(request.getTrainerId());

        TrainingType trainingType = TrainingType.builder()
                .trainingTypeName(request.getTrainingType().getTrainingTypeName())
                .build();
        Training training = trainingMapper.dtoToEntity(request, trainee, trainer, trainingType);

        return trainingMapper.entityToDto(trainingService.createTraining(training));
    }

    @Authenticated
    public TrainingResponseDTO getTrainingById(Long id) {
        return trainingMapper.entityToDto(trainingService.getTrainingById(id));
    }

    @Authenticated
    public List<TrainingResponseDTO> getAllTrainings() {
        return trainingService.getAllTrainings().stream()
                .map(trainingMapper::entityToDto)
                .toList();
    }

    @Authenticated
    public void changePassword(LoginChangeRequest request){
        userService.changePassword(request);
    }
}