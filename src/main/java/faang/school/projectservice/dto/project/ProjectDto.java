package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ProjectDto(
        Long id,
        String name,
        String description,
        Long ownerId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        ProjectStatus status,
        ProjectVisibility visibility
) {
}
