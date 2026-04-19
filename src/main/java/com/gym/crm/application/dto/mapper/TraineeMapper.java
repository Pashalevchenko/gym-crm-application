package com.gym.crm.application.dto.mapper;

import com.gym.crm.application.dto.request.TraineeRequestDTO;
import com.gym.crm.application.dto.response.TraineeResponseDTO;
import com.gym.crm.application.model.Trainee;
import org.springframework.stereotype.Component;

@Component
public class TraineeMapper {

    public Trainee dtoToEntity (TraineeRequestDTO traineeRequestDTO){
        return Trainee.builder()
                .firstName(traineeRequestDTO.getFirstName())
                .lastName(traineeRequestDTO.getLastName())
                .isActive(traineeRequestDTO.isActive())
                .dateOfBirth(traineeRequestDTO.getDateOfBirth())
                .address(traineeRequestDTO.getAddress())
                .build();
    }

    public TraineeResponseDTO entityToDto (Trainee trainee){
        return TraineeResponseDTO.builder()
                .id(trainee.getId())
                .firstName(trainee.getFirstName())
                .lastName(trainee.getLastName())
                .username(trainee.getUsername())
                .isActive(trainee.isActive())
                .dateOfBirth(trainee.getDateOfBirth())
                .address(trainee.getAddress())
                .build();
    }
}
