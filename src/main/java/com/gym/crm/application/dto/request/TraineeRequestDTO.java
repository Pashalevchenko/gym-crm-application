package com.gym.crm.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;

@Getter
@Builder
@AllArgsConstructor
public class TraineeRequestDTO {
    private final String firstName;
    private final String lastName;
    private final boolean isActive;
    private final LocalDate dateOfBirth;
    private final String address;
    private final String password;
}
