package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record ProjectDto(
        @NotNull (message = "Id must not be null") @Positive (message = "Id must be positive number") Long id,
        @NotNull (message = "Name must not be null") String name,
        String description,
        @NotNull(message = "Owner Id must not be null") Long ownerId,
        Long parentProjectId,
        ProjectStatus status,
        ProjectVisibility visibility,
        List<ProjectDto> children,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

