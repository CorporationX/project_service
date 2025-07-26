package faang.school.projectservice.dto.team;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "DTO representing a team working on a project")
public record TeamDto(

        @Schema(description = "Key to the team's avatar image stored in S3")
        String avatarKey,

        @Schema(description = "List of team members")
        List<TeamMemberDto> members
) {
}
