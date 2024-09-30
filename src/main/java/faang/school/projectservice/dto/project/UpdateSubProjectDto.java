package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record UpdateSubProjectDto(
        @NotNull Long projectId,
        ProjectStatus status,
        ProjectVisibility visibility) {
}