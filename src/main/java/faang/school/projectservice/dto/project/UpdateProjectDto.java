package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;

public record UpdateProjectDto(
        @NotBlank String name,
        @NotBlank String description,
        ProjectStatus status,
        ProjectVisibility visibility
) {
}
