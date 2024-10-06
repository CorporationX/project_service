package faang.school.projectservice.dto.team;

import faang.school.projectservice.model.TeamRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class TeamMemberDto {
    private Long teamMemberId;

    private Long userId;

    private Long teamId;

    private String nickname;

    private List<String> rolesInTeam;
}
