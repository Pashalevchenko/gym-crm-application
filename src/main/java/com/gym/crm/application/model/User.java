package com.gym.crm.application.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class User {
    private String firstName;
    private String lastName;
    private String username;

    @ToString.Exclude
    private String password;
    private boolean isActive;
}
