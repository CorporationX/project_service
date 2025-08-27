package faang.school.projectservice.dto.client.project;

import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
public record ProjectViewDto(
        Long id,
        Long ownerId,
        String name,
        String description,
        String coverImageId,
        ProjectStatus status,
        ProjectVisibility visibility
) {
}