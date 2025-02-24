package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Builder;

@Builder
public record ProjectCreateRequestDto(
        Long ownerId,
        String name,
        ProjectStatus status,
        String description
) {
}
