package faang.school.projectservice.filter.stagefilter;

import faang.school.projectservice.dto.stage.StageFilterDto;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageRoles;

import java.util.List;

public class StageFilterHelper {

    public Stage stageFirst(){
        return Stage.builder()
                .tasks(tasks())
                .stageRoles(stageRolesList())
                .build();
    }
    public Stage stageSecond(){
        return Stage.builder()
                .tasks(tasks())
                .stageRoles(stageRolesList())
                .build();
    }
    public Task task(){
        return Task.builder()
                .status(TaskStatus.TODO)
                .build();
    }

    public List<Task> tasks(){
        return List.of(task());
    }

    public List<Stage> stages(){
        return List.of(stageFirst(),stageSecond());
    }

    public StageFilterDto stageFilterDto(){
        return StageFilterDto.builder()
                .taskStatusPattern(TaskStatus.TODO)
                .teamRolePattern(TeamRole.ANALYST)
                .build();
    }

    public StageRoles stageRoles(){
        return StageRoles.builder()
                .teamRole(TeamRole.ANALYST)
                .build();
    }

    public List<StageRoles> stageRolesList(){
        return List.of(stageRoles());
    }
}
