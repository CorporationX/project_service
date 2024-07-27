package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserDto {
    private Long id;
    @NotBlank(message = "Name should not be blank")
    private String username;
    @Email(message = "Email format is incorrect")
    private String email;
}
