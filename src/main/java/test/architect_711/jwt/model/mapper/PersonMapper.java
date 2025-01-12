package test.architect_711.jwt.model.mapper;

import test.architect_711.jwt.model.dto.PersonDto;
import test.architect_711.jwt.model.entity.Person;

import java.util.function.Consumer;

public interface PersonMapper {
    default Person toEntity(PersonDto dto) {
        return new Person(
                dto.getId(),
                dto.getUsername(),
                dto.getPassword(),
                dto.getEmail()
        );
    }

    default Person toEntity(PersonDto dto, Consumer<Person> updater) {
        Person person = toEntity(dto);
        updater.accept(person);

        return person;
    }

    default PersonDto toDto(Person person) {
        return new PersonDto(
                person.getId(),
                person.getUsername(),
                person.getPassword(),
                person.getEmail(),
                person.getRole()
        );
    }
}
