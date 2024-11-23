package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeamMemberDto {
    private Long userId;

    @NotNull(message = "TeamRole can not be null")
    private TeamRole teamRole;
}
