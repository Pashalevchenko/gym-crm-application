package com.gym.crm.application.service.impl;

import com.gym.crm.application.dao.TraineeDao;
import com.gym.crm.application.dao.TrainerDao;
import com.gym.crm.application.model.User;
import com.gym.crm.application.service.ProfileService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@AllArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;

    private final Random random = new Random();

    private TraineeDao traineeDao;
    private TrainerDao trainerDao;

    @Override
    public String createUsername(String firstName, String lastname) {
        String username = firstName + "." + lastname;
        Set<String> dbUsernames = getAllUsernames();

        int userSerialNumber = 1;

        if (!dbUsernames.contains(username)) {
            return username;
        }

        while (dbUsernames.contains(username + userSerialNumber)) {
            userSerialNumber++;
        }

        return username + userSerialNumber;
    }

    @Override
    public String generatePassword() {
        StringBuilder password = new StringBuilder(PASSWORD_LENGTH);

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());

            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }

    private Set<String> getAllUsernames() {
        return Stream.concat(
                        traineeDao.findAll().stream().map(User::getUsername),
                        trainerDao.findAll().stream().map(User::getUsername)
                )
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }
}
