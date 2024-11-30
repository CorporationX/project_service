package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StageRoleDto {

    @NotNull(message = "Team role must not be null")
    private TeamRole teamRole;

    @Min(value = 1, message = "Count must be at least 1")
    private Integer count;
}
