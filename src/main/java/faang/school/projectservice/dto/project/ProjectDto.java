package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.project.ProjectStatus;
import faang.school.projectservice.model.project.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

@Builder
public record ProjectDto(
        Long id,
        @NotBlank String name,
        @NotNull @Positive Long ownerId,
        @NotNull ProjectVisibility visibility,
        @NotNull ProjectStatus status
) {
}
