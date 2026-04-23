package com.gym.crm.application.dto.mapper;

import com.gym.crm.application.dto.request.TrainerRequestDTO;
import com.gym.crm.application.dto.response.TrainerResponseDTO;
import com.gym.crm.application.model.Trainer;
import org.springframework.stereotype.Component;

@Component
public class TrainerMapper {

    public Trainer dtoToEntity(TrainerRequestDTO trainerRequestDTO){
        return Trainer.builder()
                .userId(trainerRequestDTO.getId())
                .firstName(trainerRequestDTO.getFirstName())
                .lastName(trainerRequestDTO.getLastName())
                .isActive(trainerRequestDTO.isActive())
                .specialization(trainerRequestDTO.getSpecialization())
                .build();
    }

    public TrainerResponseDTO entityToDto (Trainer trainer){
        return TrainerResponseDTO.builder()
                .id(trainer.getUserId())
                .firstName(trainer.getFirstName())
                .lastName(trainer.getLastName())
                .username(trainer.getUsername())
                .isActive(trainer.isActive())
                .specialization(trainer.getSpecialization())
                .build();
    }
}
