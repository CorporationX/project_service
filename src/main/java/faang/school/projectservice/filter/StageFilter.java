package faang.school.projectservice.filter;

import faang.school.projectservice.dto.stage.StageRequestCreateDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.Stage;

import java.util.List;

public interface StageFilter {

    boolean isApplicable(StageRequestCreateDto stageRequestCreateDto);

    List<Stage> applyByRoleAndStatus(Project project, TeamRole TeamRolefilter, TaskStatus taskStatusFilter);

}