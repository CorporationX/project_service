package faang.school.projectservice.dto.presentation;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Builder;

@Builder
public record PresentationUpdateDto(
        ProjectStatus status,
        String description
) {
}
