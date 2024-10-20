package faang.school.projectservice.model.dto.team;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateTeamDto (
        @NotNull(message = "teamMembersIds must be not null")
        List<Long> teamMembersIds,
        @NotNull(message = "projectId must be not null")
        Long projectId
) {
}
