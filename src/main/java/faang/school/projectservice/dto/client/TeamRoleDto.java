package faang.school.projectservice.dto.client;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamRoleDto {
    @NotEmpty
    private TeamRole rolePattern;
}
