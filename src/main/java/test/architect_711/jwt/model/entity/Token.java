package test.architect_711.jwt.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Table(name = "tokens")
@AllArgsConstructor @NoArgsConstructor @Data
public class Token {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "access_token", nullable = false)
    private String accessToken;

    @Column(name = "refresh_token", nullable = false)
    private String refreshToken;

    @Column(name = "is_logged_out", nullable = false)
    private boolean isLoggedOut;

    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    public Token(String accessToken, String refreshToken, Person person, boolean isLoggedOut) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.person = person;
        this.isLoggedOut = isLoggedOut;
    }
}
