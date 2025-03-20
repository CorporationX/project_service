package faang.school.projectservice.dto;

import faang.school.projectservice.model.ProjectStatus;

public record SubProjectsFilterDto(
        Long projectId,
        String name,
        ProjectStatus status
) {
}
