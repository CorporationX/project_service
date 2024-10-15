package faang.school.projectservice.model.dto;

import jakarta.validation.constraints.Min;
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
    @NotNull(message = "Team role must not be null")
    private TeamRole teamRole;

    @NotNull(message = "Count must not be null")
    @Min(value = 1, message = "Count must be at least 1")
    private Integer count;

    private Long stageId;
}