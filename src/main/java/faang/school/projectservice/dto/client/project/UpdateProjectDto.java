package faang.school.projectservice.dto.client.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;

public record UpdateProjectDto(
        String name,
        String description,
        ProjectStatus status,
        ProjectVisibility visibility
) {
}
