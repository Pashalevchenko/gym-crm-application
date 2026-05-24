package com.gym.crm.application.dao;

import com.gym.crm.application.entity.TrainingType;
import java.util.List;
import java.util.Optional;

public interface TrainingTypeDao {
    List<TrainingType> findAll();

    Optional<TrainingType> findByName(String name);
}