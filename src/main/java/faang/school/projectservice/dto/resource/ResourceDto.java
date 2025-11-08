package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ResourceStatus;
import faang.school.projectservice.model.ResourceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ResourceDto(
        @NotBlank
        String name,

        @NotBlank
        String key,

        @NotNull
        @Positive
        Long size,

        @NotNull
        ResourceType type,

        @NotNull
        ResourceStatus status,

        @NotNull
        Project project
) {
}
