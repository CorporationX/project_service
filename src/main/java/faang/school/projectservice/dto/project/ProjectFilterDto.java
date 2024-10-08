package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import jakarta.validation.constraints.Size;

public record ProjectFilterDto(
        @Size(max = 255, message = "Name pattern must not exceed 255 characters")
        String namePattern,
        ProjectStatus projectStatus
) {}
