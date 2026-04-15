package com.gym.crm.application.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Trainee extends User {
    private long id;
    private LocalDate dateOfBirth;
    private String address;
}
