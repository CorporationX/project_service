package faang.school.projectservice.dto.team;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record TeamMemberUpdateDto(
        @NotNull(message = "Roles are required")
        @NotEmpty(message = "Roles list must not be empty")
        List<@NotNull(message = "Role must not be null") TeamRole> roles) {
}
