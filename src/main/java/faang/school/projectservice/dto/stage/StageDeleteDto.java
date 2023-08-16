package faang.school.projectservice.dto.stage;

import lombok.Data;

import java.util.List;

@Data
public class StageDeleteDto {
    private Long stageId;
    private Long projectId;
    private List<Long> tasksId;
    private ActionWithTasks action;
    private Long toTransferStageId;
}
