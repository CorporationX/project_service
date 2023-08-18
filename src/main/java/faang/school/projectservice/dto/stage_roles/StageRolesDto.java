package faang.school.projectservice.dto.stage_roles;

import faang.school.projectservice.model.TeamRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Info about stage roles")
public class StageRolesDto {
    @Schema(description = "Stage role ID")
    private Long id;
    @Schema(description = "Team role")
    @NotEmpty(message = "Team role can not be empty")
    private TeamRole teamRole;
    @Schema(description = "Count of team roles")
    @NotNull(message = "Count can not be null")
    private Integer count;
}