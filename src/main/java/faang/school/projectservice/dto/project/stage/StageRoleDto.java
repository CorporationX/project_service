package faang.school.projectservice.dto.project.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record StageRoleDto(

        @NotNull(message = "Role name is required")
        TeamRole teamRole,

        @NotNull(message = "Count for member role is required")
        @Positive(message = "Count for member role must be positive")
        Integer count) {
}
