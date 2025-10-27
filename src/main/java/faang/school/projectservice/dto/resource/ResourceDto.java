package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.Project;

public record ResourceDto(
        Long id,
        String name,
        String key,
        Long size,
        String type,
        String status,
        Project project
) {
}
