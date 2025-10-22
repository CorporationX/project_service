package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateSubProjectDto(
        @NotBlank(message = "Project name must not be blank")
        String name,
        String description,
        @NotNull(message = "Project status must not be null")
        ProjectStatus status,
        @NotNull(message = "Project visibility must not be null")
        ProjectVisibility visibility
) {}
