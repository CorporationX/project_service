package faang.school.projectservice.dto.project.stage;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Builder;

@Builder
public record StageFilterDto(
        TeamRole roleFilter,
        TaskStatus taskStatusFilter,
        TaskStatusFilterType taskStatusFilterType) {}
