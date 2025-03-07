package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateSubProjectDto(
        @NotNull (message = "Id must not be null") @Positive(message = "Id must be positive number") Long id,
        ProjectStatus status,
        ProjectVisibility visibility) {
}
