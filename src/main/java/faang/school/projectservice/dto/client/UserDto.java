package faang.school.projectservice.dto.client;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UserDto(
        @NotNull (message = "Id must not be null") @Positive (message = "Id mast be positiv number") Long id,
        @NotBlank (message = "Username must not be blank") String username,
        @Email (message = "Please, enter email") String email
) {
}
