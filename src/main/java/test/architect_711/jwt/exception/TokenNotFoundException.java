package test.architect_711.jwt.exception;

import org.springframework.security.authentication.AuthenticationServiceException;

public class TokenNotFoundException extends AuthenticationServiceException {
    public TokenNotFoundException(String message) {
        super(message);
    }
}
