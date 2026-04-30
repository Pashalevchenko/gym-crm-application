package com.gym.crm.application.dao.impl;

import com.gym.crm.application.dao.TraineeDaoHibernate;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.Training;
import com.gym.crm.application.config.TransactionHandler;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class TraineeDaoHibernateImpl implements TraineeDaoHibernate {

    private final TransactionHandler transactionHandler;

    @Override
    public Trainee create(Trainee trainee) {
        Trainee created = transactionHandler.performReturningWithinTransaction(session -> {
            session.persist(trainee);
            return trainee;
        });

        log.info("Trainee with id: {} was created", created.getId());
        return created;
    }

    @Override
    public Trainee update(Trainee trainee) {
        Trainee updatedTrainee = transactionHandler.performReturningWithinTransaction(session ->
                session.merge(trainee));

        log.info("Trainee with id: {} was updated", updatedTrainee.getId());
        return updatedTrainee;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return transactionHandler.performReturningWithinTransaction(session ->
                Optional.ofNullable(session.get(Trainee.class, id)));
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        return transactionHandler.performReturningWithinTransaction(session ->
                session.createQuery("from Trainee t where t.user.username = :username", Trainee.class)
                        .setParameter("username", username)
                        .uniqueResultOptional()
        );
    }

    @Override
    public List<Trainee> findAll() {
        return transactionHandler.performReturningWithinTransaction(session ->
                session.createQuery("from Trainee", Trainee.class)
                        .getResultList());
    }

    @Override
    public void delete(Long id) {
       transactionHandler.performWithinTransaction(session -> {
            Trainee trainee = session.get(Trainee.class, id);

            if (trainee == null) {
                throw new RuntimeException(String.format("Cannot delete Trainee: ID %d not found", id));
            }

            session.remove(trainee);
        });

        log.info("Trainee with id: {} was deleted", id);
    }

    @Override
    public void deleteByUsername(String username) {
        transactionHandler.performWithinTransaction(session -> {
            Trainee trainee = session.createQuery("from Trainee t where t.user.username = :username",
                                                  Trainee.class)
                    .setParameter("username", username)
                    .uniqueResult();

            if (trainee == null) {
                throw new RuntimeException("Cannot delete Trainee: username " + username + " not found");
            }

            session.remove(trainee);
        });

        log.info("Trainee with username: {} was deleted", username);
    }

    @Override
    public List<Training> findTrainingsByCriteria(String traineeUsername,
                                                  LocalDate fromDate,
                                                  LocalDate toDate,
                                                  String trainerName,
                                                  String trainingTypeName) {
        return transactionHandler.performReturningWithinTransaction(session -> {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Training> cq = cb.createQuery(Training.class);
            Root<Training> training = cq.from(Training.class);
            List<Predicate> predicates = new ArrayList<>();

            predicates.add(cb.equal(
                    training.get("trainee").get("user").get("username"),
                    traineeUsername));

            addDateRangePredicates(cb, training, predicates, fromDate, toDate);

            if (trainerName != null) {
                predicates.add(cb.equal(
                        fullName(cb, training.get("trainer").get("user")),
                        trainerName));
            }

            if (trainingTypeName != null) {
                predicates.add(cb.equal(
                        training.get("trainingType").get("trainingTypeName"),
                        trainingTypeName));
            }

            cq.where(predicates.toArray(Predicate[]::new));

            return session.createQuery(cq).getResultList();
        });
    }

    @Override
    public List<Trainer> findNotAssignedTrainers(String traineeUsername) {
        return transactionHandler.performReturningWithinTransaction(session -> {
            String hql = """
                    from Trainer tr
                    where tr not in (
                        select assignedTrainer
                        from Trainee t
                        join t.trainers assignedTrainer
                        where t.user.username = :traineeUsername
                    )
                    """;

            return session.createQuery(hql, Trainer.class)
                    .setParameter("traineeUsername", traineeUsername)
                    .getResultList();
        });
    }

    @Override
    public Trainee updateTrainersList(String traineeUsername, Set<Trainer> trainers) {
        Trainee updatedTrainee = transactionHandler.performReturningWithinTransaction(session -> {
            Trainee trainee = session.createQuery("from Trainee t where t.user.username = :username",
                                                  Trainee.class)
                    .setParameter("username", traineeUsername)
                    .uniqueResult();

            if (trainee == null) {
                throw new RuntimeException("Trainee with username " + traineeUsername + " not found");
            }

            Set<Trainer> managedTrainers = trainers.stream()
                    .map(session::merge)
                    .collect(Collectors.toSet());

            trainee.getTrainers().clear();
            trainee.getTrainers().addAll(managedTrainers);

            return session.merge(trainee);
        });

        log.info("Trainers list for trainee username: {} was updated", traineeUsername);

        return updatedTrainee;
    }

    private void addDateRangePredicates(CriteriaBuilder cb,
                                        Root<Training> training,
                                        List<Predicate> predicates,
                                        LocalDate fromDate,
                                        LocalDate toDate) {
        if (fromDate != null) {
            predicates.add(cb.greaterThanOrEqualTo(training.get("trainingDate"), fromDate));
        }

        if (toDate != null) {
            predicates.add(cb.lessThanOrEqualTo(training.get("trainingDate"), toDate));
        }
    }

    private Expression<String> fullName(CriteriaBuilder cb, Path<?> userPath) {
        return cb.concat(cb.concat(userPath.get("firstName"), " "), userPath.get("lastName"));
    }
}