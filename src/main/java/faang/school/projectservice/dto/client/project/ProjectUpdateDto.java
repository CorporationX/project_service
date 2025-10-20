package faang.school.projectservice.dto.client.project;

import faang.school.projectservice.model.ProjectVisibility;

public record ProjectUpdateDto(
        String description,
        String status,
        ProjectVisibility visibility
){}