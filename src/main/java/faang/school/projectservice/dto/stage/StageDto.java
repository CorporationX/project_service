package faang.school.projectservice.dto.stage;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.stageroles.StageRolesDto;
import faang.school.projectservice.dto.task.TaskDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageDto {
    private long stageId;
    private String stageName;
    private ProjectDto project;
    private List<StageRolesDto> stageRoles;
    private List<TaskDto> tasks;
    private List<TeamMemberDto> executors;
}
