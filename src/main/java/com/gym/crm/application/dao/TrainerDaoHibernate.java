package com.gym.crm.application.dao;

import com.gym.crm.application.entity.Trainer;
import java.util.Optional;
import com.gym.crm.application.search.filter.TrainerTrainingSearchFilter;
import com.gym.crm.application.entity.Training;
import java.util.List;

public interface TrainerDaoHibernate extends CrudDao<Trainer, Long>{

    Trainer update(Trainer entity);

    Optional<Trainer> findByUsername(String username);

    List<Training> findTrainingsByCriteria(TrainerTrainingSearchFilter filter);
}