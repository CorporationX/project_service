package faang.school.projectservice.dto.subproject;

import faang.school.projectservice.dto.moment.*;
import faang.school.projectservice.dto.client.StageDto;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.Builder;

import java.util.List;

@Builder
public record UpdateSubProjectDto(
        List<Long> subProjectIds,
        StageDto stageDto,
        ProjectVisibility visibility,
        MomentUpdateRequestDto momentUpdateRequestDto
) {
}
