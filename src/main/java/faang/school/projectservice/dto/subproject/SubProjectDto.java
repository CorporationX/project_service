package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record SubProjectDto(
        Long id,
        String name,
        String description,
        Long ownerId,
        Long parentProjectId,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        ProjectStatus status,
        ProjectVisibility visibility) {
}