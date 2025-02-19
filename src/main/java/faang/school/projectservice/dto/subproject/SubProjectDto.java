package faang.school.projectservice.dto.subproject;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;

import java.util.List;

@Builder
public record SubProjectDto(
        Long id,
        String title,
        ProjectVisibility visibility,
        ProjectStatus status,
        //List<Long> subProjectIds
        List<SubProjectDto> subProjects
) {
}
