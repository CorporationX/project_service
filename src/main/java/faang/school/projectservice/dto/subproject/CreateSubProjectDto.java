package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateSubProjectDto(
        @NotNull
        @Positive(message = "ParentProjectId must be positive")
        Long parentProjectId,
        @NotNull(message = "Project name must not be null")
        String name,
        String description,
        ProjectVisibility visibility,
        ProjectStatus status
) {
    public CreateSubProjectDto {
        if (visibility == null) {
            visibility = ProjectVisibility.PUBLIC;
        }
        if (status == null) {
            status = ProjectStatus.CREATED;
        }
    }
}
