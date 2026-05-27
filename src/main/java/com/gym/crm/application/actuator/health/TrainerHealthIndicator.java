package com.gym.crm.application.actuator.health;

import com.gym.crm.application.repository.TrainerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainerHealthIndicator implements HealthIndicator {

    private final TrainerRepository trainerRepository;

    @Override
    public Health health() {
        try {
            long trainersCount = trainerRepository.count();

            return Health.up()
                    .withDetail("repository", "Available")
                    .withDetail("trainersCount", trainersCount)
                    .build();
        } catch (Exception exception) {
            return Health.down(exception)
                    .withDetail("repository", "Unavailable")
                    .build();
        }
    }
}