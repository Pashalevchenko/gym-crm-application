package com.gym.crm.application.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class Training {
    private long traineeId;
    private long trainerId;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private int trainingDuration;
}
