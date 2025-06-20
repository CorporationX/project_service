package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TaskStatus;
import faang.school.projectservice.model.TeamRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@Schema(description = "Team role task status")
public class TeamRoleTaskStatusDto {
    private TeamRole teamRole;
    private TaskStatus taskStatus;
}
