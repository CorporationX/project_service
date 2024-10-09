package faang.school.projectservice.model.dto.stage;

import faang.school.projectservice.model.entity.TaskStatus;
import faang.school.projectservice.model.entity.TeamRole;
import lombok.Builder;

@Builder
public record StageFilterDto(
        TeamRole role,
        TaskStatus taskStatus
) {
}
