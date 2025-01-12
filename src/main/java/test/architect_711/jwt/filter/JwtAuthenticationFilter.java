package test.architect_711.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import test.architect_711.jwt.repository.SafePersonRepository;
import test.architect_711.jwt.service.TokenService;

import java.io.IOException;
import java.util.Optional;

@Component @RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final TokenService tokenService;
    private final SafePersonRepository safePersonRepository;

    /**
     * Authenticates user based on access token passed in the request header
     * */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        Optional<String> token = TokenService.TokenExtractor.extract(request);

        if (token.isPresent())
            filterToken(token.orElse(null));

        filterChain.doFilter(request, response);
    }

    /**
     * Authenticates user
     *
     * @param token some token
     * */
    private void filterToken(@NonNull String token) {
        String username = tokenService.getExtractor().extractUsername(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null)
            authenticate(token, username);
    }

    private void authenticate(String token, String username) {
        UserDetails person = safePersonRepository.safeFindPersonByUsername(username);

        if (!tokenService.getValidator().isAccessTokenValid(token, username))
            return;

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                person.getUsername(),
                person.getPassword(),
                person.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
    }

}
