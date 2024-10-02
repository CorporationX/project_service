package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.ProjectStatus;
import lombok.Builder;

@Builder
public record ProjectFilterDto(
        String name,

        ProjectStatus projectStatus
) {
}
