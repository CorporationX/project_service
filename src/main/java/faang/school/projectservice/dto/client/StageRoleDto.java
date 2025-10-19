package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record StageRoleDto(
        @NotNull TeamRole role,
        @Min(1) int count) {
}
