package faang.school.projectservice.dto.team;

import lombok.Builder;

@Builder
public record TeamEvent(
        Long creatorId,
        Long projectId,
        Long teamId
) {
}
