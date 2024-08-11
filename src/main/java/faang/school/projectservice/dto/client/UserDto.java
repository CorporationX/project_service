package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

@Data
@Validated
public class UserDto {
    @NotNull
    private Long id;
    @NotBlank(message = "username should not be blank")
    private String username;
    @Email
    private String email;
}
