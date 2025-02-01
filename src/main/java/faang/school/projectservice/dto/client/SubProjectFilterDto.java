package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Builder;

@Builder
public record SubProjectFilterDto(
        String name,
        ProjectStatus status
) {
}
