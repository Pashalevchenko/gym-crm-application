package com.gym.crm.application;

import com.gym.crm.application.facade.GymAppFacade;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.gym.crm.application")
public class GymApp {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(GymApp.class);

        GymAppFacade facade = context.getBean(GymAppFacade.class);

        System.out.println("--- TEST 1: Виклик без авторизації ---");
        try {
            facade.changeTraineePassword("ivan.ivanov", "new_pass");
        } catch (Exception e) {
            System.out.println("Результат: Успішно перехоплено! Помилка: " + e.getMessage());
        }

        System.out.println("\n--- TEST 2: Успішний логін та зміна пароля ---");
        try {

            facade.login("ivan.ivanov", "new_secure_password6");

            facade.changeTrainerPassword("ivan.ivanov", "new_secure_password7");

            System.out.println("Результат: Тест пройдено, пароль змінено!");
        } catch (Exception e) {
            System.out.println("Помилка в TEST 2: " + e.getMessage());
            e.printStackTrace(); // Щоб бачити, де саме впало, якщо що
        }
//
        System.out.println("\n--- TEST 3: Спроба змінити чужий профіль ---");
        try {
            facade.changeTraineePassword("petro_petrov", "hacked_pass");
        } catch (Exception e) {
            System.out.println("Результат: Аспект заблокував доступ! Помилка: " + e.getMessage());
        }

        context.close();

    }
}
