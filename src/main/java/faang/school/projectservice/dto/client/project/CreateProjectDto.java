package faang.school.projectservice.dto.client.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;

public record CreateProjectDto(
        String name,
        String description,
        ProjectVisibility visibility
) {
}
