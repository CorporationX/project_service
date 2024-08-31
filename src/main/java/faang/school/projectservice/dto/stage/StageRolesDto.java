package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageRolesDto {
    private long stageId;
    private TeamRole teamRole;
    private int count;
}
