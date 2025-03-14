package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;

public record ProjectFilterDto(
        String name,
        ProjectStatus status
) {
}
