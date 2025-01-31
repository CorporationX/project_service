package faang.school.projectservice.dto.stage.stage_role;

import faang.school.projectservice.model.TeamRole;
import lombok.Data;

@Data
public class StageRolesUpdateResponseDto {
    private Long id;
    private TeamRole teamRole;
    private Integer count;
    private Long stageId;
}
