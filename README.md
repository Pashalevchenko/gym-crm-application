#Gym CRM application

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=Pashalevchenko_gym-crm-application&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=Pashalevchenko_gym-crm-application)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=Pashalevchenko_gym-crm-application&metric=coverage)](https://sonarcloud.io/summary/new_code?id=Pashalevchenko_gym-crm-application)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=Pashalevchenko_gym-crm-application&metric=bugs)](https://sonarcloud.io/summary/new_code?id=Pashalevchenko_gym-crm-application)

1. ## Database Setup (Postgresql)
Before the first run, make sure to create a database and user with proper privileges:
```
CREATE DATABASE gym_db;
CREATE USER gym WITH PASSWORD 'gym';
GRANT ALL PRIVILEGES ON DATABASE gym_db TO gym;
```

2. ## Environment Variables
Create a .env file in the root directory of the project with the following configuration:

# Database Configuration
DB_USERNAME=gym
DB_PASSWORD=gym
DB_URL=jdbc:postgresql://localhost:5432/gym_db


3. ## Run the Application

### Build the project
```
mvn clean compile
```

### Run tests (requires Docker to be running)
```
mvn test