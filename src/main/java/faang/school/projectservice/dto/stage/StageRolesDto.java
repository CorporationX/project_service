package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StageRolesDto {
    private TeamRole teamRole;
    private Integer count;
}
