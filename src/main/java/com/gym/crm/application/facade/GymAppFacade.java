package com.gym.crm.application.facade;

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

    public Trainee createTrainee(Trainee trainee){
        return traineeService.createTrainee(trainee);
    }

    public Trainee getTraineeById (Long id){
        return traineeService.getTraineeById(id);
    }

    public List<Trainee> getAllTrainees(){
        return traineeService.getAllTrainees();
    }

    public Trainee updateTrainee(Trainee trainee){
        return traineeService.updateTrainee(trainee);
    }

    public void deleteTrainee(Long id){
        traineeService.deleteTrainee(id);
    }

    public Trainer createTrainer(Trainer trainer){
        return trainerService.createTrainer(trainer);
    }

    public Trainer getTrainerById(Long id){
        return trainerService.getTrainerById(id);
    }

    public List<Trainer> getAllTrainers(){
        return trainerService.getAllTrainers();
    }

    public Trainer updateTrainer(Trainer trainer) {
        return trainerService.updateTrainer(trainer);
    }

    public Training createTraining(Training training){
        return trainingService.createTraining(training);
    }

    public Training getTrainingById(Long id){
        return trainingService.getTrainingById(id);
    }

    public List<Training> getAllTrainings(){
        return trainingService.getAllTrainings();
    }
}
