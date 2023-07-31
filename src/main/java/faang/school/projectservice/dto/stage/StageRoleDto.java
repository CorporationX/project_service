package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StageRoleDto {

    private Long stageRoleId;
    private TeamRole teamRole;
    @NotNull(message = "Count can't be null")
    private Integer count;
    private Long stageId;
}
