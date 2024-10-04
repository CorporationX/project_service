package faang.school.projectservice.dto.team;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record TeamMemberUpdateDto(
        @NotNull(message = "Team role is required")
        List<TeamRole> roles) {
}
