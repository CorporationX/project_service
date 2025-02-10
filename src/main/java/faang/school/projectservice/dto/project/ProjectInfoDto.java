package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;

import java.time.LocalDateTime;

public record ProjectInfoDto(
        long id,
        String name,
        long ownerId,
        long parentProjectId,
        ProjectStatus projectStatus,
        ProjectVisibility visibility,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}
;