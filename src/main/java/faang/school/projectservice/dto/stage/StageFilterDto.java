package faang.school.projectservice.dto.stage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageFilterDto {
    private long stageId;
    private String stageName;
    //Project
    private long projectId;
    //StageRoles
    private List<Long> stageRolesId;
    //Task
    private List<Long> tasksId;
    //TeamMember
    private List<Long> executorsId;
}
