package faang.school.projectservice.dto.teamMember;

import faang.school.projectservice.model.team.TeamRole;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateTeamMemberDto(
        @Positive @NotNull Long userId,
        @NotEmpty List<TeamRole> roles,
        @NotEmpty List<Long> stageIds
) {
}
