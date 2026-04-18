package com.gym.crm.application.dao;

import com.gym.crm.application.model.Trainer;

public interface TrainerDao extends CrudDao<Trainer, Long> {

    Trainer update(Trainer entity);
}
