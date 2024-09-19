package faang.school.projectservice.dto.project.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record StageRoleDto(
        Long id,

        @NotNull(message = "role name is required")
        TeamRole teamRole,

        @NotNull(message = "count for member role is required")
        @Positive(message = "count for member role must be positive")
        Integer count,

        Long stageId) {
}
