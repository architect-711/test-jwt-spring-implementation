package test.architect_711.jwt.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import test.architect_711.jwt.model.dto.PersonDto;
import test.architect_711.jwt.model.dto.PersonDtoValidationGroup;
import test.architect_711.jwt.model.dto.TokenDto;
import test.architect_711.jwt.model.entity.Person;
import test.architect_711.jwt.model.entity.Role;
import test.architect_711.jwt.model.mapper.PersonMapper;
import test.architect_711.jwt.repository.SafePersonRepository;
import test.architect_711.jwt.repository.TokenRepository;

@Service @Validated
public class AuthenticationService extends TokenService implements PersonMapper {
    private final SafePersonRepository safePersonRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(TokenRepository tokenRepository, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, SafePersonRepository safePersonRepository) {
        super(tokenRepository);
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.safePersonRepository = safePersonRepository;
    }

    /**
     * Saves new person based on person DTO
     *
     * @param personDto person's credentials
     * @return saved credentials
     * */
    public ResponseEntity<PersonDto> register(@Validated(PersonDtoValidationGroup.Creation.class) PersonDto personDto) {
        Person person = toEntity(personDto, p -> {
            p.setPassword(passwordEncoder.encode(personDto.getPassword()));
            p.setRole(Role.USER);
        });

        return ResponseEntity.ok(toDto(safePersonRepository.save(person)));
    }

    /**
     * Generates new access and refresh tokens, alongside with refreshTokens() method
     * make previous tokens loggedOut
     *
     * @param personDto person's credentials
     * @return access and refresh tokens
     * */
    public ResponseEntity<TokenDto> login(@NonNull PersonDto personDto) {
        String username = personDto.getUsername();

        // This herr will find person by username and compare its password with passed
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, personDto.getPassword()));

        return ResponseEntity.ok(updater.fullUpdate(safePersonRepository.safeFindPersonByUsername(username)));
    }

    /**
     * Extracts refresh token from the Authorization header and
     * generates new access and refresh tokens. Makes previous tokens logged out.
     *
     * @param request the request
     * @return new access and refresh tokens
     * */
    public ResponseEntity<TokenDto> refreshTokens(HttpServletRequest request) {
        String refreshToken = TokenExtractor.safeExtract(request);
        Person person = safePersonRepository.safeFindPersonByUsername(extractor.extractUsername(refreshToken));

        return !validator.isRefreshTokenValid(refreshToken, person.getUsername()) ?
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).build() :
                ResponseEntity.ok(updater.fullUpdate(person));
    }


}
