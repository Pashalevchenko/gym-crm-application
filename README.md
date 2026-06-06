# Gym CRM application

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Pashalevchenko_gym-crm-application&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Pashalevchenko_gym-crm-application)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Pashalevchenko_gym-crm-application&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Pashalevchenko_gym-crm-application)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Pashalevchenko_gym-crm-application&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Pashalevchenko_gym-crm-application)

## 1. Prerequisites

Before running the application, make sure the following tools are installed:

```text
Java 21
Maven
PostgreSQL
Redis
```

## 2. Clone the project

```bash
git clone https://github.com/Pashalevchenko/gym-crm-application.git
cd gym-crm-application
```

## 3. Database Setup PostgreSQL

Before the first run, create a database and user with proper privileges:

```sql
CREATE DATABASE gym_db;
CREATE USER gym WITH PASSWORD 'gym';
GRANT ALL PRIVILEGES ON DATABASE gym_db TO gym;
```

## 4. Environment Variables

Create a `.env` file in the root directory of the project with the following configuration:

```text
DB_USERNAME=gym
DB_PASSWORD=gym
DB_URL=jdbc:postgresql://localhost:5432/gym_db

JWT_SECRET=your-256-bit-secret-key-your-256-bit-secret-key
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:5173
```
`CORS_ALLOWED_ORIGINS` defines frontend origins that are allowed to access the API from a browser.

## 5. Build the project

```bash
mvn clean compile
```

## 6. Run tests

Docker must be running before executing tests.

```bash
mvn test
```

## 7. Run the application from console

```bash
mvn spring-boot:run
```

If you want to run with a specific Spring profile, use:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

After startup, the application will be available at:

```text
http://localhost:8080/gym-crm-application
```


## 8.  Actuator endpoints

The application exposes Spring Boot Actuator endpoints for health checks and Prometheus metrics.

Base local URL:

```text
http://localhost:8080/gym-crm-application
```

Available actuator endpoints:

```text
GET /actuator/health
GET /actuator/health/database
GET /actuator/health/trainee
GET /actuator/health/trainer
GET /actuator/health/trainingType
GET /actuator/health/userRepository
GET /actuator/metrics
GET /actuator/prometheus
```

### Examples

Check application health:

```bash
http://localhost:8080/gym-crm-application/actuator/health
```

Check database health:

```bash
http://localhost:8080/gym-crm-application/actuator/health/database
```

Check trainee health:

```bash
http://localhost:8080/gym-crm-application/actuator/health/trainee
```

Check trainer health:

```bash
http://localhost:8080/gym-crm-application/actuator/health/trainer
```

Check Prometheus metrics:

```bash
http://localhost:8080/gym-crm-application/actuator/prometheus
```

Metric descriptions:

```text
gym_trainees_total     - total number of created trainees since application startup
gym_trainers_total     - total number of created trainers since application startup
gym_trainings_total    - total number of created trainings since application startup
gym_trainees_active    - current number of active trainees
gym_trainers_active    - current number of active trainers
```