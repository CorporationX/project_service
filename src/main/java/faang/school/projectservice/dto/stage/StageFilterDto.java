package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Data;

@Data
public class StageFilterDto {
    private TeamRole teamRole;
    private TaskStatus allTasksInStatus;
    private TaskStatus anyTaskInStatus;
}
