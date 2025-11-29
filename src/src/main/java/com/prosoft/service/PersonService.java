package com.prosoft.service;

import com.prosoft.model.Person;
import com.prosoft.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonService {

    private final PersonRepository personRepository;

    // CRUD операции
    public Person save(Person person) {
        log.info("Saving person: {} {}", person.getFirstName(), person.getLastName());
        return personRepository.save(person);
    }

    public List<Person> saveAll(List<Person> persons) {
        log.info("Saving {} persons", persons.size());
        return (List<Person>) personRepository.saveAll(persons);
    }

    public Optional<Person> findById(String id) {
        log.debug("Finding person by id: {}", id);
        return personRepository.findById(id);
    }

    public List<Person> findAll() {
        log.debug("Finding all persons");
        // В Spring Data 2.7.x используем Iterable
        Iterable<Person> persons = personRepository.findAll();
        List<Person> result = new ArrayList<>();
        persons.forEach(result::add);
        return result;
    }

    public void deleteById(String id) {
        log.info("Deleting person by id: {}", id);
        personRepository.deleteById(id);
    }

    // Поисковые методы
    public List<Person> findByFirstName(String firstName) {
        log.debug("Finding persons by first name: {}", firstName);
        return personRepository.findByFirstName(firstName);
    }

    public List<Person> findByLastName(String lastName) {
        log.debug("Finding persons by last name: {}", lastName);
        return personRepository.findByLastName(lastName);
    }

    public List<Person> findByAgeBetween(Integer min, Integer max) {
        log.debug("Finding persons by age range: {} - {}", min, max);
        return personRepository.findByAgeBetween(min, max);
    }

    public List<Person> findByCity(String city) {
        log.debug("Finding persons by city: {}", city);
        return personRepository.findByCity(city);
    }

    public List<Person> findByProfession(String profession) {
        log.debug("Finding persons by profession: {}", profession);
        return personRepository.findByProfession(profession);
    }

    public List<Person> searchInBio(String text) {
        log.debug("Searching in bio: {}", text);
        return personRepository.findByBioContaining(text);
    }

    public List<Person> findByCityAndAgeGreaterThan(String city, Integer age) {
        log.debug("Finding persons by city {} and age greater than {}", city, age);
        return personRepository.findByCityAndAgeGreaterThan(city, age);
    }

    // Поиск по всем текстовым полям
    public List<Person> searchEverywhere(String query) {
        log.debug("Global search: {}", query);
        return personRepository.findByFirstNameContainingOrLastNameContainingOrBioContaining(
                query, query, query);
    }
}