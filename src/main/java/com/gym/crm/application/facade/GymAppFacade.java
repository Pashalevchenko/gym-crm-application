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
import com.gym.crm.application.service.TraineeService;
import com.gym.crm.application.service.TrainerService;
import com.gym.crm.application.service.TrainingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GymAppFacade {

    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    private final TraineeMapper traineeMapper;
    private final TrainerMapper trainerMapper;
    private final TrainingMapper trainingMapper;

    public TraineeResponseDTO createTrainee(TraineeRequestDTO request){
        Trainee trainee = traineeMapper.dtoToEntity(request);

        return traineeMapper.entityToDto(traineeService.createTrainee(trainee));
    }

    public TraineeResponseDTO getTraineeById (Long id){
        return traineeMapper.entityToDto(traineeService.getTraineeById(id));
    }

    public List<TraineeResponseDTO> getAllTrainees(){
        return traineeService.getAllTrainees().stream()
                .map(traineeMapper::entityToDto)
                .toList();
    }

    public TraineeResponseDTO updateTrainee(TraineeRequestDTO request){
        Trainee trainee = traineeMapper.dtoToEntity(request);

        return traineeMapper.entityToDto(traineeService.updateTrainee(trainee));
    }

    public void deleteTrainee(Long id){
        traineeService.deleteTrainee(id);
    }

    public TrainerResponseDTO createTrainer(TrainerRequestDTO request){
        Trainer trainer = trainerMapper.dtoToEntity(request);

        return trainerMapper.entityToDto(trainerService.createTrainer(trainer));
    }

    public TrainerResponseDTO getTrainerById(Long id){
        return trainerMapper.entityToDto(trainerService.getTrainerById(id));
    }

    public List<TrainerResponseDTO> getAllTrainers(){
        return trainerService.getAllTrainers().stream()
                .map(trainerMapper::entityToDto)
                .toList();
    }

    public TrainerResponseDTO updateTrainer(TrainerRequestDTO request) {
        Trainer trainer = trainerMapper.dtoToEntity(request);

        return trainerMapper.entityToDto(trainerService.updateTrainer(trainer));
    }

    public TrainingResponseDTO createTraining(TrainingRequestDTO request){
        Training training = trainingMapper.dtoToEntity(request);

        return trainingMapper.entityToDto(trainingService.createTraining(training));
    }

    public TrainingResponseDTO getTrainingById(Long id){
        return trainingMapper.entityToDto(trainingService.getTrainingById(id));
    }

    public List<TrainingResponseDTO> getAllTrainings(){
        return trainingService.getAllTrainings().stream()
                .map(trainingMapper::entityToDto)
                .toList();
    }
}
