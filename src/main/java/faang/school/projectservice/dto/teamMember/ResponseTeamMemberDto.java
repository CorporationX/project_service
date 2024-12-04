package faang.school.projectservice.dto.teamMember;

import faang.school.projectservice.model.team.TeamRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.util.List;

@Builder
public record ResponseTeamMemberDto(
        @Positive @NotNull Long id,
        @Positive @NotNull Long userId,
        @NotBlank List<TeamRole> roles,
        @Positive @NotNull Long teamId,
        @NotBlank List<@Positive Long> stageIds
) {
}
