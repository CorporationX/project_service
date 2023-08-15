package faang.school.projectservice.dto.stage;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.task.TaskDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StageDto {
    private Long stageId;
    private String stageName;
    private ProjectDto project;
    private List<TaskDto> tasks;
    private List<StageRolesDto> stageRoles;
}
