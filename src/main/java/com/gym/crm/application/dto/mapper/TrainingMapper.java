package com.gym.crm.application.dto.mapper;

import com.gym.crm.application.dto.request.TrainingRequestDTO;
import com.gym.crm.application.dto.response.TrainingResponseDTO;
import com.gym.crm.application.model.Training;
import org.springframework.stereotype.Component;

@Component
public class TrainingMapper {

    public Training dtoToEntity(TrainingRequestDTO trainingRequestDTO){
        return Training.builder()
                .traineeId(trainingRequestDTO.getTraineeId())
                .trainerId(trainingRequestDTO.getTrainerId())
                .trainingName(trainingRequestDTO.getTrainingName())
                .trainingType(trainingRequestDTO.getTrainingType())
                .trainingDate(trainingRequestDTO.getTrainingDate())
                .trainingDuration(trainingRequestDTO.getTrainingDuration())
                .build();
    }

    public TrainingResponseDTO entityToDto (Training training){
        return TrainingResponseDTO.builder()
                .traineeId(training.getTraineeId())
                .trainerId(training.getTrainerId())
                .trainingName(training.getTrainingName())
                .trainingType(training.getTrainingType())
                .trainingDate(training.getTrainingDate())
                .trainingDuration(training.getTrainingDuration())
                .build();
    }
}