package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateSubProjectDto(
        List<SubProjectDto> subProjectDtos,
        ProjectVisibility visibility
) {
}
