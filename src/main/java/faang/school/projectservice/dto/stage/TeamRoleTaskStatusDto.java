package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class TeamRoleTaskStatusDto {
    private TeamRole teamRole;
    private TaskStatus taskStatus;
}
