package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.validation.ValidationConstants;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record CreateProjectDto(
        @NotBlank(message = "Name must not be blank")
        @Size(max = ValidationConstants.NAME_MAX_LENGTH, message = ValidationConstants.NAME_SIZE_MESSAGE)
        String name,

        @NotBlank(message = "Description must not be blank")
        @Size(max = ValidationConstants.DESCRIPTION_MAX_LENGTH, message = ValidationConstants.DESCRIPTION_SIZE_MESSAGE)
        String description,

        @NotNull
        ProjectVisibility visibility,

        @Nullable
        Long parentProjectId
) {
}