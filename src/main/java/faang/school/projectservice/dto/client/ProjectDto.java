package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;

public record ProjectDto(
        Long id,
        String name,
        Long ownerId,
        ProjectStatus status,
        ProjectVisibility visibility
) {
}