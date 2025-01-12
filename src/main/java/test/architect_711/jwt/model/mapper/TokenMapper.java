package test.architect_711.jwt.model.mapper;

import test.architect_711.jwt.model.dto.TokenDto;
import test.architect_711.jwt.model.entity.Person;
import test.architect_711.jwt.model.entity.Token;

public interface TokenMapper {
    default TokenDto toDto(Token token) {
        return new TokenDto(token.getAccessToken(), token.getRefreshToken());
    }

    default Token toEntity(TokenDto tokenDto, Person owner, boolean isLoggedOut) {
        return new Token(tokenDto.getAccessToken(), tokenDto.getRefreshToken(), owner, isLoggedOut);
    }
}
