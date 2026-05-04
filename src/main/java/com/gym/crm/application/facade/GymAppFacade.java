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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class GymAppFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;
    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;

    public TraineeResponseDTO createTrainee(TraineeRequestDTO request) {
        Trainee trainee = traineeMapper.dtoToEntity(request);

        return traineeMapper.entityToDto(traineeService.createTrainee(trainee));
    }

    public TraineeResponseDTO getTraineeById(Long id) {
        return traineeMapper.entityToDto(traineeService.getTraineeById(id));
    }

    public TraineeResponseDTO getTraineeByUsername(String username) {
        return traineeMapper.entityToDto(traineeService.getTraineeByUsername(username));
    }

    public List<TraineeResponseDTO> getAllTrainees() {
        return traineeService.getAllTrainees().stream()
                .map(traineeMapper::entityToDto)
                .toList();
    }

    public TraineeResponseDTO updateTrainee(TraineeRequestDTO request) {
        Trainee trainee = traineeMapper.dtoToEntity(request);

        return traineeMapper.entityToDto(traineeService.updateTrainee(trainee));
    }

    public void changeTraineePassword(String username, String newPassword) {
        traineeService.changePassword(username, newPassword);
    }

    public TraineeResponseDTO activateTrainee(String username) {
        return traineeMapper.entityToDto(traineeService.activateTrainee(username));
    }

    public TraineeResponseDTO deactivateTrainee(String username) {
        return traineeMapper.entityToDto(traineeService.deactivateTrainee(username));
    }

    public void deleteTrainee(Long id) {
        traineeService.deleteTrainee(id);
    }

    public void deleteTraineeByUsername(String username) {
        traineeService.deleteTraineeByUsername(username);
    }

    public List<TrainingResponseDTO> getTraineeTrainings(String username, LocalDate fromDate, LocalDate toDate, String trainerName, String trainingTypeName) {
        return traineeService.getTraineeTrainings(username, fromDate, toDate, trainerName, trainingTypeName).stream()
                .map(trainingMapper::entityToDto)
                .toList();
    }

    public List<TrainerResponseDTO> getNotAssignedTrainers(String traineeUsername) {
        return traineeService.getNotAssignedTrainers(traineeUsername).stream()
                .map(trainerMapper::entityToDto)
                .toList();
    }

    public TraineeResponseDTO updateTraineeTrainersList(String traineeUsername, Set<Trainer> trainers) {
        return traineeMapper.entityToDto(
                traineeService.updateTrainersList(traineeUsername, trainers)
        );
    }

    public TrainerResponseDTO createTrainer(TrainerRequestDTO request) {
        Trainer trainer = trainerMapper.dtoToEntity(request);

        return trainerMapper.entityToDto(trainerService.createTrainer(trainer));
    }

    public TrainerResponseDTO getTrainerById(Long id) {
        return trainerMapper.entityToDto(trainerService.getTrainerById(id));
    }

    public TrainerResponseDTO getTrainerByUsername(String username) {
        return trainerMapper.entityToDto(trainerService.getTrainerByUsername(username));
    }

    public List<TrainerResponseDTO> getAllTrainers() {
        return trainerService.getAllTrainers().stream()
                .map(trainerMapper::entityToDto)
                .toList();
    }

    public TrainerResponseDTO updateTrainer(TrainerRequestDTO request) {
        Trainer trainer = trainerMapper.dtoToEntity(request);

        return trainerMapper.entityToDto(trainerService.updateTrainer(trainer));
    }

    public void changeTrainerPassword(String username, String newPassword) {
        trainerService.changePassword(username, newPassword);
    }

    public TrainerResponseDTO activateTrainer(String username) {
        return trainerMapper.entityToDto(trainerService.activateTrainer(username));
    }

    public TrainerResponseDTO deactivateTrainer(String username) {
        return trainerMapper.entityToDto(trainerService.deactivateTrainer(username));
    }

    public List<TrainingResponseDTO> getTrainerTrainings(String username, LocalDate fromDate, LocalDate toDate, String traineeName) {
        return trainerService.getTrainerTrainings(username, fromDate, toDate, traineeName).stream()
                .map(trainingMapper::entityToDto)
                .toList();
    }

    public TrainingResponseDTO createTraining(TrainingRequestDTO request) {
        Trainee trainee = traineeService.getTraineeById(request.getTraineeId());
        Trainer trainer = trainerService.getTrainerById(request.getTrainerId());

        TrainingType trainingType = TrainingType.builder()
                .trainingTypeName(request.getTrainingType().getTrainingTypeName())
                .build();
        Training training = trainingMapper.dtoToEntity(request, trainee, trainer, trainingType);

        return trainingMapper.entityToDto(trainingService.createTraining(training));
    }

    public TrainingResponseDTO getTrainingById(Long id) {
        return trainingMapper.entityToDto(trainingService.getTrainingById(id));
    }

    public List<TrainingResponseDTO> getAllTrainings() {
        return trainingService.getAllTrainings().stream()
                .map(trainingMapper::entityToDto)
                .toList();
    }
}