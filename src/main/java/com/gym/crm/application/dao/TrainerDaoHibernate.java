package com.gym.crm.application.dao;

import com.gym.crm.application.entity.Trainer;
import java.util.Optional;

public interface TrainerDaoHibernate extends CrudDao<Trainer, Long>{

    Trainer update(Trainer entity);

    Optional<Trainer> findByUsername(String username);
}