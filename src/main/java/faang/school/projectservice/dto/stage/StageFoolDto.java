package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.StageRoles;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageFoolDto {
    private long stageId;
    private String stageName;
    private Project project;
    private List<StageRoles> stageRoles;
    private List<Task> tasks;
    private List<TeamMember> executors;
}
