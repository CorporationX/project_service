package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StageRolesDto {
    private Long id;

    @NotNull
    private TeamRole teamRole;

    @NotNull
    private Integer count;

    private Long stageId;
}
