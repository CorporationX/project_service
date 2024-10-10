package faang.school.projectservice.model.dto.project;

import faang.school.projectservice.model.entity.ProjectStatus;
import lombok.Builder;

@Builder
public record ProjectFilterDto(
        String name,

        ProjectStatus projectStatus
) {
}
