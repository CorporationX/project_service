package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;

public record CreateProjectDto(
        @NotBlank String name,
        @NotBlank String description,
        ProjectVisibility visibility
) {
}
