package faang.school.projectservice.dto.client.project;

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
    ProjectStatus status,
    ProjectVisibility visibility,
    LocalDateTime createdAt,
    LocalDateTime updatedAt

){}
