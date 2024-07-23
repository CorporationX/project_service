package faang.school.projectservice.dto;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Task;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.stage.StageRoles;
import lombok.Data;

import java.util.List;

@Data
public class StageDto {
    private long stageId;
    private String stageName;
    private Project project;
    private List<StageRoles> stageRoles;
    private List<Long> taskIds;
    private List<Long> executorIds;

}
