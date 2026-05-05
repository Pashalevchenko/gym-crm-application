package com.gym.crm.application.dao.impl;

import com.gym.crm.application.dao.TraineeDao;
import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.Training;
import com.gym.crm.application.search.TraineeTrainingQueryBuilder;
import com.gym.crm.application.search.filter.TraineeTrainingSearchFilter;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j
@Repository
@RequiredArgsConstructor
public class TraineeDaoImpl implements TraineeDao {

    // Видаляємо TransactionHandler, використовуємо SessionFactory
    private final SessionFactory sessionFactory;
    private final TraineeTrainingQueryBuilder traineeTrainingQueryBuilder;

    private Session getSession() {
        // Отримуємо сесію, яку вже підготував для нас TransactionAspect
        return sessionFactory.getCurrentSession();
    }

    @Override
    public Trainee create(Trainee trainee) {
        getSession().persist(trainee);
        log.info("Trainee with id: {} was created", trainee.getId());
        return trainee;
    }

    @Override
    public Trainee update(Trainee trainee) {
        Trainee updatedTrainee = getSession().merge(trainee);
        log.info("Trainee with id: {} was updated", updatedTrainee.getId());
        return updatedTrainee;
    }

    @Override
    public Optional<Trainee> findById(Long id) {
        return Optional.ofNullable(getSession().get(Trainee.class, id));
    }

    @Override
    public Optional<Trainee> findByUsername(String username) {
        return getSession().createQuery("from Trainee t where t.user.username = :username", Trainee.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    @Override
    public List<Trainee> findAll() {
        return getSession().createQuery("from Trainee", Trainee.class)
                .getResultList();
    }

    @Override
    public List<Training> findTrainingsByCriteria(TraineeTrainingSearchFilter filter) {
        CriteriaBuilder cb = getSession().getCriteriaBuilder();
        CriteriaQuery<Training> query = traineeTrainingQueryBuilder.build(cb, filter);
        return getSession().createQuery(query).getResultList();
    }

    @Override
    public void delete(Long id) {
        Trainee trainee = getSession().get(Trainee.class, id);
        if (trainee != null) {
            getSession().remove(trainee);
            log.info("Trainee with id: {} was deleted", id);
        }
    }

    @Override
    public void deleteByUsername(String username) {
        Trainee trainee = getSession().createQuery("from Trainee t where t.user.username = :username", Trainee.class)
                .setParameter("username", username)
                .uniqueResult();
        if (trainee != null) {
            getSession().remove(trainee);
            log.info("Trainee with username: {} was deleted", username);
        }
    }

    @Override
    public List<Trainer> findNotAssignedTrainers(String traineeUsername) {
        String hql = """
                from Trainer tr
                where tr not in (
                    select assignedTrainer
                    from Trainee t
                    join t.trainers assignedTrainer
                    where t.user.username = :traineeUsername
                )
                """;
        return getSession().createQuery(hql, Trainer.class)
                .setParameter("traineeUsername", traineeUsername)
                .getResultList();
    }

    @Override
    public Trainee updateTrainersList(String traineeUsername, Set<Trainer> trainers) {
        Trainee trainee = getSession().createQuery("from Trainee t where t.user.username = :username", Trainee.class)
                .setParameter("username", traineeUsername)
                .uniqueResult();

        if (trainee == null) {
            throw new RuntimeException("Trainee with username " + traineeUsername + " not found");
        }

        Set<Trainer> managedTrainers = trainers.stream()
                .map(getSession()::merge)
                .collect(Collectors.toSet());

        trainee.getTrainers().clear();
        trainee.getTrainers().addAll(managedTrainers);

        return getSession().merge(trainee);
    }
}