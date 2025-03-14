package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;

@Builder
public record ProjectDto(
        Long id,
        String name,
        String description,
        Long ownerId,
        ProjectStatus status,
        ProjectVisibility visibility
) {
}
