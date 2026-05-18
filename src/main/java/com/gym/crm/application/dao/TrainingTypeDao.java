package com.gym.crm.application.dao;

import com.gym.crm.application.entity.TrainingType;
import java.util.List;

public interface TrainingTypeDao {
    List<TrainingType> findAll();
}