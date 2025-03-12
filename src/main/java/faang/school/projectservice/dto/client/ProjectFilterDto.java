package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.ProjectStatus;

public record ProjectFilterDto(
        String name,
        ProjectStatus status
) {
}
