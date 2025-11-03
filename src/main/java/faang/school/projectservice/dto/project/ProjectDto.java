package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;

public record ProjectDto(
        Long id,
        String name,
        String description,
        ProjectStatus status,
        ProjectVisibility visibility
) {
}
