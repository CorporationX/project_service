package faang.school.projectservice.dto.client.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record ProjectUpdateDto(
        @Size(max = 4096)
        String description,
        ProjectStatus status,
        ProjectVisibility visibility
){}