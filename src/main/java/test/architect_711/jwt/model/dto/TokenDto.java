package test.architect_711.jwt.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class TokenDto {
    private String accessToken;
    private String refreshToken;
}
