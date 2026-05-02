package com.gym.crm.application.dao;

import com.gym.crm.application.search.filter.TraineeTrainingSearchFilter;
import com.gym.crm.application.entity.Training;
import java.util.List;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import java.util.Optional;
import java.util.Set;

public interface TraineeDaoHibernate extends CrudDao<Trainee, Long>{

    void delete(Long id);

    void deleteByUsername(String username);

    Trainee update(Trainee entity);

    Optional<Trainee> findByUsername(String username);

    List<Training> findTrainingsByCriteria(TraineeTrainingSearchFilter filter);

    List<Trainer> findNotAssignedTrainers(String traineeUsername);

    Trainee updateTrainersList(String traineeUsername, Set<Trainer> trainers);
}