package com.prosoft.repository;

import com.prosoft.model.Person;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PersonRepository extends ElasticsearchRepository<Person, String> {

    List<Person> findByFirstName(String firstName);
    List<Person> findByLastName(String lastName);
    List<Person> findByAgeBetween(Integer minAge, Integer maxAge);
    List<Person> findByCity(String city);
    List<Person> findByProfession(String profession);
    List<Person> findByBioContaining(String text);
    List<Person> findByCityAndAgeGreaterThan(String city, Integer age);
    List<Person> findByFirstNameContainingOrLastNameContainingOrBioContaining(
            String firstName, String lastName, String bio);
}