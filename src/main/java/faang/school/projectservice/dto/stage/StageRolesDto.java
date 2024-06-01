package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import lombok.Data;

@Data
public class StageRolesDto {
    private Long id;
    private TeamRole teamRole;
    private Integer count;
    private Long stageId;
}
