package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;

import java.time.LocalDateTime;

public record ProjectDtoResponse(
        long id,
        String name,
        long ownerId,
        long parentProjectId,
        ProjectStatus status,
        ProjectVisibility visibility,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
