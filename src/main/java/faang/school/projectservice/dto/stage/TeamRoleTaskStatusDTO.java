package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TeamRoleTaskStatusDTO {
    TeamRole teamRole;
    TaskStatus taskStatus;
}
