package com.gym.crm.application.repository.specification;

import com.gym.crm.application.entity.Training;
import com.gym.crm.application.search.filter.TraineeTrainingSearchFilter;
import com.gym.crm.application.search.filter.TrainerTrainingSearchFilter;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public final class TrainingSpecifications {

    private TrainingSpecifications() {
    }

    public static Specification<Training> byTrainerCriteria(TrainerTrainingSearchFilter filter) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (filter.getUsername() != null && !filter.getUsername().isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("trainer").get("user").get("username"), filter.getUsername()));
            }

            if (filter.getFromDate() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("trainingDate"), filter.getFromDate()));
            }

            if (filter.getToDate() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("trainingDate"), filter.getToDate()));
            }

            if (filter.getTraineeName() != null && !filter.getTraineeName().isBlank()) {
                Join<Object, Object> traineeUser = root.join("trainee").join("user");

                var fullName = cb.concat(cb.concat(traineeUser.get("firstName"), " "), traineeUser.get("lastName"));

                predicate = cb.and(predicate,
                        cb.like(cb.lower(fullName), "%" + filter.getTraineeName().toLowerCase() + "%"));
            }

            return predicate;
        };
    }

    public static Specification<Training> byTraineeCriteria(TraineeTrainingSearchFilter filter) {
        return (root, query, cb) -> {
            var predicate = cb.conjunction();

            if (filter.getUsername() != null && !filter.getUsername().isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("trainee").get("user").get("username"), filter.getUsername()));
            }

            if (filter.getFromDate() != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("trainingDate"), filter.getFromDate()));
            }

            if (filter.getToDate() != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("trainingDate"), filter.getToDate()));
            }

            if (filter.getTrainerName() != null && !filter.getTrainerName().isBlank()) {
                Join<Object, Object> trainerUser = root.join("trainer").join("user");

                var fullName = cb.concat(cb.concat(trainerUser.get("firstName"), " "), trainerUser.get("lastName"));

                predicate = cb.and(predicate, cb.like(cb.lower(fullName), "%" + filter.getTrainerName().toLowerCase() + "%")
                );
            }

            if (filter.getTrainingTypeName() != null && !filter.getTrainingTypeName().isBlank()) {
                predicate = cb.and(predicate, cb.equal(root.get("trainingType").get("trainingTypeName"), filter.getTrainingTypeName())
                );
            }

            return predicate;
        };
    }
}