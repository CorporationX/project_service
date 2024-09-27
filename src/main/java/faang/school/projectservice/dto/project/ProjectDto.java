package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.validator.groups.CreateGroup;
import faang.school.projectservice.validator.groups.UpdateGroup;
import jakarta.validation.constraints.*;
import lombok.Builder;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Builder
@Validated
public record ProjectDto(

        @Null(message = "Id must be null", groups = {CreateGroup.class, UpdateGroup.class})
        Long id,

        @NotBlank(message = "Name can not be null or empty")
        @Size(max = 128, message = "Name must be less than 128 characters")
        String name,

        @NotBlank
        @Size(max = 4096, message = "Description must be less than 4096 characters")
        String description,

        @NotNull
        @Positive
        Long ownerId,

        @NotNull
        @Positive
        Long parentProjectId,

        LocalDateTime createdAt,

        LocalDateTime updatedAt,

        @NotNull
        ProjectStatus status,

        @NotNull
        ProjectVisibility visibility
) {
}