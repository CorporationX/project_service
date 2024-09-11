package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;

@Builder
public record StageRolesDto(
//        Long id,

        @NotNull
        TeamRole teamRole,

        @PositiveOrZero
        Integer count/*,

        Long stageId*/
) {
}
