package faang.school.projectservice.dto.team;

import faang.school.projectservice.model.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.util.List;

@Builder
public record TeamMemberCreateDto(
        @NotNull(message = "user id is required")
        long userId,
        List<TeamRole> roles) {
}
