package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Builder;

@Builder
public record StageFilterDto(
        TeamRole role,
        TaskStatus taskStatus
) {
}
