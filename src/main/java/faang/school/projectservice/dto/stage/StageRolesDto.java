package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageRolesDto {
    private Long id;
    private TeamRole teamRole;
    private Integer count;
}