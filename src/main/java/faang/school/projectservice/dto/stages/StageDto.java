package faang.school.projectservice.dto.stages;

import faang.school.projectservice.dto.project.ProjectDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageDto {
    private Long stageId;
    private String stageName;
    private ProjectDto project;
}
