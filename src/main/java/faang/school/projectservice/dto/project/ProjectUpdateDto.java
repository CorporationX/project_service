package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.annotation.Nullable;
import lombok.Builder;

@Builder
public record  ProjectUpdateDto(
        @Nullable
        String description,

        @Nullable
        ProjectStatus status,

        @Nullable
        ProjectVisibility visibility
) {
}
