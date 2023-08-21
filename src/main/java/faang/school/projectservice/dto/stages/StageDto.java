package faang.school.projectservice.dto.stages;

import faang.school.projectservice.model.Project;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StageDto {
    private Long stageId;
    private String stageName;
    private Project project;
}
