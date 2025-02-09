package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateSubProjectDto(
        Long id,
        List<Long> subProjectIds,
        StageDto stageDto,
        ProjectVisibility visibility,
        MomentDto lastUpdate
) {
}
