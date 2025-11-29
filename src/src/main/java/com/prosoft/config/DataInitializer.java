package com.prosoft.config;

import com.prosoft.model.Person;
import com.prosoft.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PersonService personService;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Очистка старых данных
            List<Person> existingPersons = personService.findAll();
            if (!existingPersons.isEmpty()) {
                log.info("Cleaning up {} existing persons", existingPersons.size());
                for (Person person : existingPersons) {
                    personService.deleteById(person.getId());
                }
            }

            // Создание тестовых данных
            List<Person> persons = Arrays.asList(
                    new Person(null, "John", "Doe", 30, "Developer",
                            "New York", "Java developer with Spring Boot experience"),
                    new Person(null, "Jane", "Smith", 25, "Designer",
                            "San Francisco", "UI/UX designer passionate about user experience"),
                    new Person(null, "Mike", "Johnson", 35, "Manager",
                            "New York", "Project manager with agile methodology expertise"),
                    new Person(null, "Sarah", "Brown", 28, "Developer",
                            "Boston", "Full-stack developer working with React and Spring"),
                    new Person(null, "Alex", "Wilson", 32, "Architect",
                            "San Francisco", "Software architect specializing in microservices")
            );

            List<Person> savedPersons = personService.saveAll(persons);
            log.info("Demo data initialized with {} persons", savedPersons.size());

        } catch (Exception e) {
            log.error("Error during data initialization: {}", e.getMessage());
            // Не бросаем исключение дальше, чтобы приложение продолжало работать
        }
    }
}