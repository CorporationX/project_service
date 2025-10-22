package faang.school.projectservice.dto.client.project;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Size;

public record ProjectUpdateDto(
        @Size(max = 4096)
        String description,
        String status,
        ProjectVisibility visibility
){}