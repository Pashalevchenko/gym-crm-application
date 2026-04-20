package com.gym.crm.application.dto.response;

import com.gym.crm.application.model.TrainingType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@Builder
@ToString
public class TrainerResponseDTO {
    private final String firstName;
    private final String lastName;
    private final String username;
    private final boolean isActive;
    private final TrainingType specialization;
}
