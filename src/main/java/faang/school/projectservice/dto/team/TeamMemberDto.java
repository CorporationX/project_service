package faang.school.projectservice.dto.team;

import faang.school.projectservice.model.TeamRole;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Information about a specific team member")
public record TeamMemberDto(

        @Schema(description = "Nickname of the team member")
        String nickname,

        @Schema(description = "Roles assigned to the team member")
        List<TeamRole> roles
) {
}
