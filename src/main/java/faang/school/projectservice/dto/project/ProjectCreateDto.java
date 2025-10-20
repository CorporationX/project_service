package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ProjectCreateDto(
        @NotBlank
        String name,

        @NotBlank
        String description,

        @NotNull
        ProjectVisibility visibility
) {
}
