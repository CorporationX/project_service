package faang.school.projectservice.dto.team;

import faang.school.projectservice.model.TeamRole;
import lombok.Builder;

import java.util.List;


@Builder
public record TeamMemberDto(
        Long userId,
        String nickname,
        TeamRole role,
        Long teamId
) {
}