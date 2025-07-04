package faang.school.projectservice.bjs_77845.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {

    @NotBlank(message = "must not be blank")
    private String name;

    @Email(message = "must be a valid email address")
    private String email;

    @NotNull(message = "must not be null")
    private String address;

}