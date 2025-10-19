package faang.school.projectservice.dto.client.project;

import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProjectCreateDto (
        @NotBlank
        @Size(max = 128)
        String name,
        @Size(max = 4096)
        String description,
        ProjectVisibility visibility
) {}
