package com.gym.crm.application.dto.request;

import com.gym.crm.application.entity.TrainingType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TrainerRequestDTO {
    private final long id;
    private final String firstName;
    private final String lastName;
    private final boolean isActive;
    private final TrainingType specialization;
    private final String password;
}