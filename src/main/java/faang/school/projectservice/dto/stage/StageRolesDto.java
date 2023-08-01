package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StageRolesDto {

    private Long stageRoleId;
    @NotNull(message = "Team role can't be null" )
    private TeamRole teamRole;
    @NotNull(message = "Count can't be null")
    private Integer count;
}
