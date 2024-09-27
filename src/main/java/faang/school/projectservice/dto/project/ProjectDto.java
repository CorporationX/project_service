package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.validator.groups.CreateGroup;
import faang.school.projectservice.validator.groups.UpdateGroup;
import jakarta.validation.constraints.*;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProjectDto(

        @Null(message = "Id must be null", groups = {CreateGroup.class, UpdateGroup.class})
        Long id,

        @NotBlank(message = "Name can not be null or empty", groups = {CreateGroup.class, UpdateGroup.class})
        @Size(max = 128, message = "Name must be less than 128 characters", groups = {CreateGroup.class, UpdateGroup.class})
        String name,

        @NotBlank(groups = {CreateGroup.class, UpdateGroup.class})
        @Size(max = 4096, message = "Description must be less than 4096 characters", groups = {CreateGroup.class, UpdateGroup.class})
        String description,

        @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
        @Positive(groups = {CreateGroup.class, UpdateGroup.class})
        Long ownerId,

        @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
        @Positive(groups = {CreateGroup.class, UpdateGroup.class})
        Long parentProjectId,

        LocalDateTime createdAt,

        LocalDateTime updatedAt,

        @NotNull(groups = {CreateGroup.class})
        ProjectStatus status,

        @NotNull(groups = {CreateGroup.class, UpdateGroup.class})
        ProjectVisibility visibility
) {
}