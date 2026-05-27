package com.gym.crm.application.actuator.health;

import com.gym.crm.application.repository.TrainingTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrainingTypeHealthIndicator implements HealthIndicator {

    private final TrainingTypeRepository trainingTypeRepository;

    @Override
    public Health health() {
        try {
            long count = trainingTypeRepository.count();

            if (count > 0) {
                return Health.up()
                        .withDetail("repository", "Available")
                        .withDetail("trainingTypesCount", count)
                        .build();
            }

            return Health.down()
                    .withDetail("repository", "Available")
                    .withDetail("trainingTypesCount", count)
                    .withDetail("reason", "No training types found")
                    .build();
        } catch (Exception exception) {
            return Health.down(exception)
                    .withDetail("repository", "Unavailable")
                    .build();
        }
    }
}