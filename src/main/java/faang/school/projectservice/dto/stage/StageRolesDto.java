package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;


@Builder
public record StageRolesDto(
        @NotNull Long id,
        TeamRole teamRole,
        @Positive Integer count
) {
}