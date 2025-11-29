package com.prosoft.controller;

import com.prosoft.model.Person;
import com.prosoft.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/persons")
@RequiredArgsConstructor
public class PersonController {

    private final PersonService personService;

    @PostMapping
    public Person createPerson(@RequestBody Person person) {
        log.info("Creating new person: {} {}", person.getFirstName(), person.getLastName());
        return personService.save(person);
    }

    @PostMapping("/bulk")
    public List<Person> createPersons(@RequestBody List<Person> persons) {
        log.info("Creating {} persons in bulk", persons.size());
        return personService.saveAll(persons);
    }

    @GetMapping
    public List<Person> getAllPersons() {
        log.debug("Getting all persons");
        return personService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable String id) {
        log.debug("Getting person by id: {}", id);
        return personService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/first-name")
    public List<Person> getByFirstName(@RequestParam String firstName) {
        log.debug("Searching by first name: {}", firstName);
        return personService.findByFirstName(firstName);
    }

    @GetMapping("/search/last-name")
    public List<Person> getByLastName(@RequestParam String lastName) {
        log.debug("Searching by last name: {}", lastName);
        return personService.findByLastName(lastName);
    }

    @GetMapping("/search/age-range")
    public List<Person> getByAgeRange(@RequestParam Integer min,
                                      @RequestParam Integer max) {
        log.debug("Searching by age range: {} - {}", min, max);
        return personService.findByAgeBetween(min, max);
    }

    @GetMapping("/search/city")
    public List<Person> getByCity(@RequestParam String city) {
        log.debug("Searching by city: {}", city);
        return personService.findByCity(city);
    }

    @GetMapping("/search/profession")
    public List<Person> getByProfession(@RequestParam String profession) {
        log.debug("Searching by profession: {}", profession);
        return personService.findByProfession(profession);
    }

    @GetMapping("/search/bio")
    public List<Person> searchInBio(@RequestParam String text) {
        log.debug("Searching in bio: {}", text);
        return personService.searchInBio(text);
    }

    @GetMapping("/search/global")
    public List<Person> searchEverywhere(@RequestParam String q) {
        log.debug("Global search: {}", q);
        return personService.searchEverywhere(q);
    }

    @GetMapping("/search/city-age")
    public List<Person> getByCityAndAge(@RequestParam String city,
                                        @RequestParam Integer age) {
        log.debug("Searching by city {} and age > {}", city, age);
        return personService.findByCityAndAgeGreaterThan(city, age);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePerson(@PathVariable String id) {
        log.info("Deleting person by id: {}", id);
        personService.deleteById(id);
        return ResponseEntity.ok().build();
    }
}