package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ProjectCreateDto(
        @NotNull
        String name,

        @NotNull
        String description,

        @NotNull
        Long ownerId,

        @NotNull
        ProjectVisibility visibility
) {
}
