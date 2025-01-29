package faang.school.projectservice.dto.client;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Data Transfer UserDto for User details")
public record UserDto(
        @Schema(description = "Unique identifier of the user", example = "123")
        Long id,

        @Schema(description = "Full name of the user", example = "Alice Blue")
        String username,

        @Schema(description = "Email address of the user", example = "alice@email.com")
        String email
) {
}
