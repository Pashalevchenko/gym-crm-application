package com.gym.crm.application.dao;

import com.gym.crm.application.model.Trainee;

public interface TraineeDao extends CrudDao<Trainee, Long> {

    void delete(Long id);

    Trainee update(Trainee entity);
}
