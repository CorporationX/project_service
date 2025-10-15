package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;

public record ProjectUpdateDto(
        @NotNull
        String name,

        @NotNull
        String description,

        @Nullable
        Long ownerId,

        @NotNull
        ProjectStatus status,

        @NotNull
        ProjectVisibility visibility
) {
}
