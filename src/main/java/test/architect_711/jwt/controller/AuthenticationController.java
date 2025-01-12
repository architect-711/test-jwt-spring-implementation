package test.architect_711.jwt.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import test.architect_711.jwt.model.dto.PersonDto;
import test.architect_711.jwt.model.dto.TokenDto;
import test.architect_711.jwt.service.AuthenticationService;

@RestController @RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    /**
     * Saves new person
     *
     * @param personDto person's credentials required for saving
     * @return what was saved
     * */
    @PostMapping("/registration")
    public ResponseEntity<PersonDto> registration(@RequestBody PersonDto personDto) {
        return authenticationService.register(personDto);
    }

    /**
     * Generates access and refresh tokens based on provided credentials
     *
     * @param personDto credentials
     * @return access and refresh tokens
     * */
    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody PersonDto personDto) {
        return authenticationService.login(personDto);
    }

    /**
     * Generates new refresh and access token, previous makes logged out.
     *
     * @param request the request
     * @return new refresh and access token
     * */
    @PostMapping("/refresh_tokens")
    public ResponseEntity<TokenDto> refreshTokens(HttpServletRequest request) {
        return authenticationService.refreshTokens(request);
    }
}
