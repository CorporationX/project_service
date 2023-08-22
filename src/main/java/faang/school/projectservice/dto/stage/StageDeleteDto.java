package faang.school.projectservice.dto.stage;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StageDeleteDto {
    private Long stageId;
    private Long projectId;
    private List<Long> tasksId;
    private TaskAction action;
    private Long toTransferStageId;
}
