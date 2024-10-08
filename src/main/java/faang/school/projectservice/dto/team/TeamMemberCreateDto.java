package faang.school.projectservice.dto.team;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record TeamMemberCreateDto(
        @NotNull(message = "User ID is required")
        Long userId,
        @NotNull(message = "Roles are required")
        @NotEmpty(message = "Roles list must not be empty")
        List<@NotNull(message = "Role must not be null") TeamRole> roles) {
}
