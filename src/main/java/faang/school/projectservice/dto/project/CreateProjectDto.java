package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;

public record CreateProjectDto(
        String name,
        String description,
        ProjectVisibility visibility
) {
}
