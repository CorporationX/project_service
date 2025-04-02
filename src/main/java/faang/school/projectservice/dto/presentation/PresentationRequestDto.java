package faang.school.projectservice.dto.presentation;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Builder;

@Builder
public record PresentationRequestDto(
        Long ownerId,
        String name,
        ProjectStatus status,
        String description
) {
}
