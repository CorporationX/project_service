package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProjectDto(
        @NotNull Long id,
        @NotBlank String name,
        @NotBlank String description,
        ProjectStatus status,
        ProjectVisibility visibility
) {
}
