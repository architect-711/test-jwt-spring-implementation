package test.architect_711.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import test.architect_711.jwt.exception.TokenNotFoundException;
import test.architect_711.jwt.model.dto.TokenDto;
import test.architect_711.jwt.model.entity.Person;
import test.architect_711.jwt.model.entity.Token;
import test.architect_711.jwt.model.mapper.TokenMapper;
import test.architect_711.jwt.repository.TokenRepository;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Data @Component
public class TokenService {
    @Value("${security.jwt.secret_key}")
    private String secretKey;
    @Value("${security.jwt.access_token_expiration}")
    private long accessTokenExpiration;
    @Value("${security.jwt.refresh_token_expiration}")
    private long refreshTokenExpiration;

    @NonNull
    private final TokenRepository tokenRepository;

    protected final TokenExtractor extractor = new TokenExtractor();
    protected final TokenValidator validator = new TokenValidator();
    protected final TokenGenerator generator = new TokenGenerator();
    protected final TokenUpdater updater = new TokenUpdater();

    /**
     * Generates token signing key, required for signature part
     * */
    public SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    public class TokenExtractor {
        private final static String AUTHORIZATION_HEADER = "Authorization";

        /**
         * Extracts token from Authorization header
         *
         * @param request the request
         * @return an empty Optional if the header isn't found or it's empty, otherwise Optional with header's value
         * */
        public static Optional<String> extract(@NonNull HttpServletRequest request) {
            String extractHeader = request.getHeader(AUTHORIZATION_HEADER);

            return (extractHeader == null || !extractHeader.startsWith("Bearer ")) ? Optional.empty() : Optional.of(extractHeader.substring(7));
        }

        /**
         * Returns extracted token or throws the exception
         *
         * @param request the http request
         * @return token
         * @throws TokenNotFoundException if token not found
         * */
        public static String safeExtract(@NonNull HttpServletRequest request) {
            return extract(request).orElseThrow(() -> new TokenNotFoundException("Token not found."));
        }

        /**
         * Extracts username from token
         *
         * @param token any token
         * @return username
         * */
        public String extractUsername(String token) {
            return extractPayload(token).getSubject();
        }

        /**
         * Extracts expiration time from token
         *
         * @param token any token
         * @return Date it was created
         * */
        public Date extractExpirationTime(String token) {
            return extractPayload(token).getExpiration();
        }

        private Claims extractPayload(String token) {
            JwtParserBuilder parser = Jwts.parser();

            parser.verifyWith(getSigningKey());

            return parser.build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
    }

    public class TokenValidator {
        public boolean isAccessTokenValid(String accessToken, String username) {
            return isValid(accessToken, tokenRepository.findByAccessToken(accessToken), username);
        }

        public boolean isRefreshTokenValid(String refreshToken, String username) {
            return isValid(refreshToken, tokenRepository.findByRefreshToken(refreshToken), username);
        }

        private boolean isValid(String token, Optional<Token> ambivalentToken, String username) {
            return username.equals(extractor.extractUsername(token))
                    && isNotExpired(token)
                    && isUnloggedOut(ambivalentToken);
        }

        public boolean isNotExpired(String token) {
            return extractor.extractExpirationTime(token).after(new Date());
        }

        public boolean isUnloggedOut(Optional<Token> ambivalent) {
            return ambivalent.map(token -> !token.isLoggedOut()).orElse(false);
        }

    }

    public class TokenGenerator {
        public String generateAccessToken(String username) {
            return generateToken(username, accessTokenExpiration);
        }

        public String generateRefreshToken(String username) {
            return generateToken(username, refreshTokenExpiration);
        }

        public String generateToken(String username, long expiryTime) {
            return Jwts.builder()
                    .subject(username)
                    .issuedAt(new Date())
                    .expiration(new Date(System.currentTimeMillis() + expiryTime))
                    .signWith(getSigningKey())
                    .compact();
        }
    }

    public class TokenUpdater implements TokenMapper {
        /**
         * Revokes (i.e. makes logged out) all unlogged out person's token
         *
         * @param personId id of person whom tokens to revoke
         * */
        public void revokeAllTokens(Long personId) {
            List<Token> tokens = tokenRepository.findAllUnloggedTokensByPersonId(personId);

            if (tokens.isEmpty())
                return;

            tokens.forEach(token -> token.setLoggedOut(true));

            tokenRepository.saveAll(tokens);
        }

        /**
         * Saves new access and refresh tokens
         *
         * @param accessToken the access token
         * @param refreshToken the refresh token
         * @param person their owner
         * @return Token the saved entity
         * */
        public Token saveNewTokens(String accessToken, String refreshToken, Person person) {
            return tokenRepository.save(new Token(
                    accessToken,
                    refreshToken,
                    person,
                    false
            ));
        }

        /**
         * Revokes all tokens and saves new ones.
         *
         * @param person owner
         * @return TokenDto contains saved tokens
         * */
        public TokenDto fullUpdate(@NonNull Person person) {
            revokeAllTokens(person.getId());

            return toDto(saveNewTokens(
                    generator.generateAccessToken(person.getUsername()),
                    generator.generateRefreshToken(person.getUsername()),
                    person
            ));
        }

    }

}
