package faang.school.projectservice.dto.filter.stage;

import faang.school.projectservice.dto.filter.FilterDto;
import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.*;

import java.util.List;

@Builder
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageFilterDto extends FilterDto {
    private String stageName;
    private Long projectId;
    private List<TeamRole> teamRoles;
    private List<TaskStatus> taskStatuses;
}
