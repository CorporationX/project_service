package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;

@Builder
public record CreateSubProjectDto(
        Long parentId,
        ProjectVisibility visibility,
        String name,
        String description,
        ProjectStatus status
) {}