package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record  ProjectUpdateDto(
        @Nullable @NotBlank
        String name,

        @Nullable @NotBlank
        String description,

        @Nullable
        ProjectStatus status,

        @Nullable
        ProjectVisibility visibility
) {
}
