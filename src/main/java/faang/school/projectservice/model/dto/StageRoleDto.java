package faang.school.projectservice.model.dto;

import faang.school.projectservice.model.enums.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StageRoleDto {
    private Long id;
    @NotNull
    private TeamRole teamRole;
    @NotNull
    private Integer count;
    private Long stageId;
}

