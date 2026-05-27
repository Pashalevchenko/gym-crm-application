package com.gym.crm.application.actuator.health;

import com.gym.crm.application.repository.TraineeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TraineeHealthIndicator implements HealthIndicator {

    private final TraineeRepository traineeRepository;

    @Override
    public Health health() {
        try {
            long traineesCount = traineeRepository.count();

            return Health.up()
                    .withDetail("repository", "Available")
                    .withDetail("traineesCount", traineesCount)
                    .build();
        } catch (Exception exception) {
            return Health.down(exception)
                    .withDetail("repository", "Unavailable")
                    .build();
        }
    }
}