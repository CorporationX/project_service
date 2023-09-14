package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StageRolesDto {
    private Long id;

    @NotNull(message = "Team role cannot be null")
    private TeamRole teamRole;

    @NotNull(message = "Count cannot be null")
    private Integer count;
}
