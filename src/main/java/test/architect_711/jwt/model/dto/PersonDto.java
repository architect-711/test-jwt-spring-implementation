package test.architect_711.jwt.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import test.architect_711.jwt.model.entity.Role;

@Data @AllArgsConstructor @NoArgsConstructor
public class PersonDto {
    @NotNull(message =  "Id can't be null")
    @Null(message = "Id must be null", groups = PersonDtoValidationGroup.Creation.class)
    private Long id;
    @NotBlank(message = "Username can't be blank")
    private String username;
    @NotBlank(message = "Password can't be blank")
    private String password;
    @NotBlank(message = "Email can't be blank")
    private String email;
    @NotBlank(message = "Role can't be blank")
    @Null(message = "Role must be null.", groups = PersonDtoValidationGroup.Creation.class)
    private Role role;
}
