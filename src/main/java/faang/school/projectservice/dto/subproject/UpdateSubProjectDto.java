package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.project.ProjectStatus;
import faang.school.projectservice.model.project.ProjectVisibility;
import lombok.Builder;

@Builder
public record UpdateSubProjectDto(
        ProjectStatus status,
        ProjectVisibility visibility
) {
}
