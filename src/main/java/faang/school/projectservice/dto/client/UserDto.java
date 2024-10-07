package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class UserDto {

    @Positive
    private Long id;

    @NotBlank(message = "Name should not be blank")
    private String username;

    @Email(message = "Email must be in right format")
    private String email;
}
