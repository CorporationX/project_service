package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.StageRoles;

import java.util.List;

public record StageDto(
        Long stageId,
        String stageName,
        Project project,
        List<StageRoles> stageRoles,
        List <Task> tasks,
        List<TeamMember> executors
) {
}
