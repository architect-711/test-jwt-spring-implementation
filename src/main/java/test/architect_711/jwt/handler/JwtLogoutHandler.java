package test.architect_711.jwt.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;
import test.architect_711.jwt.repository.TokenRepository;
import test.architect_711.jwt.service.TokenService;

@Component @RequiredArgsConstructor
public class JwtLogoutHandler implements LogoutHandler {
    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
         String accessToken = TokenService.TokenExtractor.safeExtract(request);

         tokenRepository.findByAccessToken(accessToken).ifPresent(t -> {
             t.setLoggedOut(true);
             tokenRepository.save(t);
         });

    }

}
