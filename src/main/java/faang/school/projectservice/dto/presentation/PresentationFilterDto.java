package faang.school.projectservice.dto.presentation;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Builder;

@Builder
public record PresentationFilterDto(
        String name,
        ProjectStatus status
) {
}
