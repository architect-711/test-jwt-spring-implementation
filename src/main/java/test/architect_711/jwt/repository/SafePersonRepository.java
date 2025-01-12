package test.architect_711.jwt.repository;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import test.architect_711.jwt.model.entity.Person;

public interface SafePersonRepository extends PersonRepository {
    default Person safeFindPersonByUsername(String username) {
        return findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Person not found with username: "+ username));
    }
}
