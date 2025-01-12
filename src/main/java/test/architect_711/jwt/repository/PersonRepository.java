package test.architect_711.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.architect_711.jwt.model.entity.Person;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    Optional<Person> findByUsername(String username);

    Optional<Person> findByEmail(String email);
}
