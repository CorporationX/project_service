package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class StageRolesDto {
    private Long id;
    private TeamRole teamRole;
    private Integer count;
    private Long stageId;
}