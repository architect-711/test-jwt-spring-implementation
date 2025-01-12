package test.architect_711.jwt.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import test.architect_711.jwt.model.entity.Token;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    @Query(
            nativeQuery = true,
            value = """ 
                select tokens.id, tokens.access_token, tokens.refresh_token, tokens.is_logged_out, tokens.person_id from tokens
                inner join people on tokens.id = people.id
                where tokens.person_id = :id
                and tokens.is_logged_out = false;
                """
    )
    List<Token> findAllUnloggedTokensByPersonId(Long id);

    Optional<Token> findByRefreshToken(String refreshToken);

    Optional<Token> findByAccessToken(String accessToken);
}
