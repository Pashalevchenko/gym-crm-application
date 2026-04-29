package com.gym.crm.application.dao;

import com.gym.crm.application.entity.Trainee;
import com.gym.crm.application.entity.Trainer;
import com.gym.crm.application.entity.Training;
import com.gym.crm.application.entity.TrainingType;
import com.gym.crm.application.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Trainee Hibernate DAO integration tests")
class TraineeDaoHibernateImplTest extends AbstractDaoIntegrationTest {

    @Autowired
    private TraineeDaoHibernate traineeDao;

    @Autowired
    private SessionFactory sessionFactory;

    @Nested
    @DisplayName("create")
    class CreateTests {

        @Test
        @DisplayName("Should save trainee with related user")
        void create_success() {
            Trainee trainee = buildTrainee("Borys", "Burpee", "borys.burpee");

            Trainee created = traineeDao.create(trainee);

            assertThat(created.getId()).isNotNull();
            assertThat(created.getUser().getId()).isNotNull();

            Optional<Trainee> found = traineeDao.findById(created.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getUser().getUsername()).isEqualTo("borys.burpee");
            assertThat(found.get().getDateOfBirth()).isEqualTo(LocalDate.of(2000, 1, 1));
            assertThat(found.get().getAddress()).isEqualTo("Kyiv");
        }

        @Test
        @DisplayName("Should throw exception when trainee is null")
        void create_nullTrainee() {
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> traineeDao.create(null));

            assertThat(exception).isNotNull();
        }
    }

    @Nested
    @DisplayName("update")
    class UpdateTests {

        @Test
        @DisplayName("Should update trainee when trainee exists")
        void update_success() {
            Trainee created = traineeDao.create(buildTrainee("Olha", "Overheadpress", "olha.overheadpress"));
            created.setAddress("Lviv");

            Trainee updated = traineeDao.update(created);
            Optional<Trainee> found = traineeDao.findById(updated.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getAddress()).isEqualTo("Lviv");
            assertThat(found.get().getUser().getUsername()).isEqualTo("olha.overheadpress");
        }

        @Test
        @DisplayName("Should throw exception when trainee is null")
        void update_nullTrainee() {
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> traineeDao.update(null));

            assertThat(exception).isNotNull();
        }
    }

    @Nested
    @DisplayName("findById")
    class FindByIdTests {

        @Test
        @DisplayName("Should return trainee when trainee with requested id exists")
        void findById_found() {
            Trainee created = traineeDao.create(buildTrainee("Marta", "Muscle", "marta.muscle"));
            Optional<Trainee> found = traineeDao.findById(created.getId());

            assertThat(found).isPresent();
            assertThat(found.get().getUser().getUsername()).isEqualTo("marta.muscle");
        }

        @Test
        @DisplayName("Should return empty optional when trainee with requested id does not exist")
        void findById_notFound() {
            Optional<Trainee> found = traineeDao.findById(999L);

            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByUsername")
    class FindByUsernameTests {

        @Test
        @DisplayName("Should return trainee when trainee with requested username exists")
        void findByUsername_found() {
            traineeDao.create(buildTrainee("Tolik", "Treadmill", "tolik.treadmill"));
            Optional<Trainee> found = traineeDao.findByUsername("tolik.treadmill");

            assertThat(found).isPresent();
            assertThat(found.get().getUser().getFirstName()).isEqualTo("Tolik");
        }

        @Test
        @DisplayName("Should return empty optional when trainee with requested username does not exist")
        void findByUsername_notFound() {
            Optional<Trainee> found = traineeDao.findByUsername("ghost.gains");

            assertThat(found).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAllTests {

        @Test
        @DisplayName("Should return all trainees")
        void findAll_success() {
            traineeDao.create(buildTrainee("Nina", "NoPain", "nina.nopain"));
            traineeDao.create(buildTrainee("Petro", "Protein", "petro.protein"));

            List<Trainee> trainees = traineeDao.findAll();

            assertThat(trainees).hasSize(2);
            assertThat(trainees)
                    .extracting(trainee -> trainee.getUser().getUsername())
                    .containsExactlyInAnyOrder("nina.nopain", "petro.protein");
        }

        @Test
        @DisplayName("Should return empty list when there are no trainees")
        void findAll_empty() {
            List<Trainee> trainees = traineeDao.findAll();

            assertThat(trainees).isEmpty();
        }
    }

    @Nested
    @DisplayName("delete")
    class DeleteTests {

        @Test
        @DisplayName("Should delete trainee when trainee with requested id exists")
        void delete_success() {
            Trainee created = traineeDao.create(buildTrainee("Roman", "Rowing", "roman.rowing"));
            traineeDao.delete(created.getId());

            assertThat(traineeDao.findById(created.getId())).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception when trainee with requested id does not exist")
        void delete_notFound() {
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> traineeDao.delete(999L));

            assertThat(exception.getMessage()).contains("not found");
        }
    }

    @Nested
    @DisplayName("deleteByUsername")
    class DeleteByUsernameTests {

        @Test
        @DisplayName("Should delete trainee when trainee with requested username exists")
        void deleteByUsername_success() {
            Trainee created = traineeDao.create(buildTrainee("Sofia", "Squat", "sofia.squat"));
            traineeDao.deleteByUsername("sofia.squat");

            assertThat(traineeDao.findById(created.getId())).isEmpty();
            assertThat(traineeDao.findByUsername("sofia.squat")).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception when trainee with requested username does not exist")
        void deleteByUsername_notFound() {
            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> traineeDao.deleteByUsername("missing.muscle"));

            assertThat(exception.getMessage()).contains("not found");
        }
    }

    @Nested
    @DisplayName("findTrainingsByCriteria")
    class FindTrainingsByCriteriaTests {

        @Test
        @DisplayName("Should return trainings matching criteria")
        void findTrainingsByCriteria_success() {
            Trainee trainee = traineeDao.create(buildTrainee("Vasyl", "Velocity", "vasyl.velocity"));
            TrainingType trainingType = createTrainingType("Penguin Yoga");
            Trainer trainer = createTrainer("Pavlo", "Plank", "pavlo.plank", trainingType);

            createTraining(
                    "Morning Penguin Stretch",
                    LocalDate.of(2026, 4, 10),
                    60,
                    trainingType,
                    trainee,
                    trainer
            );

            List<Training> trainings = traineeDao.findTrainingsByCriteria(
                    "vasyl.velocity",
                    LocalDate.of(2026, 4, 1),
                    LocalDate.of(2026, 4, 30),
                    "Pavlo Plank",
                    "Penguin Yoga"
            );

            assertThat(trainings).hasSize(1);
            assertThat(trainings.get(0).getTrainingName()).isEqualTo("Morning Penguin Stretch");
        }

        @Test
        @DisplayName("Should return empty list when criteria do not match")
        void findTrainingsByCriteria_noMatch() {
            Trainee trainee = traineeDao.create(buildTrainee("Max", "Mountainclimber", "max.mountainclimber"));
            TrainingType trainingType = createTrainingType("Fitness Fiesta");
            Trainer trainer = createTrainer("Ira", "Iron", "ira.iron", trainingType);

            createTraining(
                    "April Fitness Fiesta",
                    LocalDate.of(2026, 4, 10),
                    50,
                    trainingType,
                    trainee,
                    trainer
            );

            List<Training> trainings = traineeDao.findTrainingsByCriteria(
                    "max.mountainclimber",
                    LocalDate.of(2026, 5, 1),
                    LocalDate.of(2026, 5, 30),
                    null,
                    null
            );

            assertThat(trainings).isEmpty();
        }
    }

    @Nested
    @DisplayName("findNotAssignedTrainers")
    class FindNotAssignedTrainersTests {

        @Test
        @DisplayName("Should return trainers not assigned to trainee")
        void findNotAssignedTrainers_success() {
            traineeDao.create(buildTrainee("Katya", "Kettlebell", "katya.kettlebell"));

            TrainingType trainingType = createTrainingType("Strength Shenanigans");
            Trainer assignedTrainer = createTrainer("Hlib", "Heavyweight", "hlib.heavyweight", trainingType);
            createTrainer("Fedir", "Foamroller", "fedir.foamroller", trainingType);

            traineeDao.updateTrainersList("katya.kettlebell", Set.of(assignedTrainer));

            List<Trainer> result = traineeDao.findNotAssignedTrainers("katya.kettlebell");

            assertThat(result)
                    .extracting(trainer -> trainer.getUser().getUsername())
                    .contains("fedir.foamroller")
                    .doesNotContain("hlib.heavyweight");
        }

        @Test
        @DisplayName("Should return empty list when all trainers are assigned")
        void findNotAssignedTrainers_empty() {
            traineeDao.create(buildTrainee("Yana", "Yoga", "yana.yoga"));
            TrainingType trainingType = createTrainingType("Balance Bonanza");

            Trainer trainerOne = createTrainer("Artem", "Abs", "artem.abs", trainingType);
            Trainer trainerTwo = createTrainer("Mira", "Mat", "mira.mat", trainingType);
            traineeDao.updateTrainersList("yana.yoga", Set.of(trainerOne, trainerTwo));

            List<Trainer> result = traineeDao.findNotAssignedTrainers("yana.yoga");

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("updateTrainersList")
    class UpdateTrainersListTests {

        @Test
        @DisplayName("Should replace trainee trainers list with provided trainers")
        void updateTrainersList_success() {
            traineeDao.create(buildTrainee("Denys", "Deadlift", "denys.deadlift"));
            TrainingType trainingType = createTrainingType("Powerlifting Party");

            Trainer trainerOne = createTrainer("Taras", "Triceps", "taras.triceps", trainingType);
            Trainer trainerTwo = createTrainer("Zlata", "Zumba", "zlata.zumba", trainingType);

            traineeDao.updateTrainersList(
                    "denys.deadlift",
                    Set.of(trainerOne, trainerTwo)
            );

            Trainee found = findTraineeWithTrainersByUsername("denys.deadlift");

            assertThat(found.getTrainers()).hasSize(2);
            assertThat(found.getTrainers())
                    .extracting(trainer -> trainer.getUser().getUsername())
                    .containsExactlyInAnyOrder("taras.triceps", "zlata.zumba");
        }

        @Test
        @DisplayName("Should throw exception when trainee with requested username does not exist")
        void updateTrainersList_traineeNotFound() {
            TrainingType trainingType = createTrainingType("Invisible Cardio");
            Trainer trainer = createTrainer("Bohdan", "Benchpress", "bohdan.benchpress", trainingType);

            RuntimeException exception = assertThrows(RuntimeException.class,
                    () -> traineeDao.updateTrainersList("unknown.unicorn", Set.of(trainer)));

            assertThat(exception.getMessage()).contains("not found");
        }
    }

    private Trainee buildTrainee(String firstName, String lastName, String username) {
        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .password("12345")
                .isActive(true)
                .build();

        return Trainee.builder()
                .user(user)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .address("Kyiv")
                .build();
    }

    private TrainingType createTrainingType(String name) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            TrainingType trainingType = TrainingType.builder()
                    .trainingTypeName(name)
                    .build();

            session.persist(trainingType);
            session.getTransaction().commit();

            return trainingType;
        }
    }

    private Trainer createTrainer(String firstName, String lastName, String username, TrainingType specialization) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            TrainingType managedSpecialization = session.merge(specialization);

            User user = User.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .username(username)
                    .password("12345")
                    .isActive(true)
                    .build();
            Trainer trainer = Trainer.builder()
                    .user(user)
                    .specialization(managedSpecialization)
                    .build();

            session.persist(trainer);
            session.getTransaction().commit();

            return trainer;
        }
    }

    private Training createTraining(
            String trainingName,
            LocalDate trainingDate,
            Integer duration,
            TrainingType trainingType,
            Trainee trainee,
            Trainer trainer
    ) {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();

            TrainingType managedTrainingType = session.merge(trainingType);
            Trainee managedTrainee = session.merge(trainee);
            Trainer managedTrainer = session.merge(trainer);

            Training training = Training.builder()
                    .trainingName(trainingName)
                    .trainingDate(trainingDate)
                    .trainingDuration(duration)
                    .trainingType(managedTrainingType)
                    .trainee(managedTrainee)
                    .trainer(managedTrainer)
                    .build();

            session.persist(training);
            session.getTransaction().commit();

            return training;
        }
    }

    private Trainee findTraineeWithTrainersByUsername(String username) {
        try (Session session = sessionFactory.openSession()) {
            return session.createQuery(
                            """
                            select distinct t
                            from Trainee t
                            left join fetch t.trainers trainer
                            left join fetch trainer.user
                            where t.user.username = :username
                            """,
                            Trainee.class
                    )
                    .setParameter("username", username)
                    .uniqueResult();
        }
    }
}