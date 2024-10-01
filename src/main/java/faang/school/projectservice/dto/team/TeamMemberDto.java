package faang.school.projectservice.dto.team;

import faang.school.projectservice.model.TeamRole;

import java.util.List;

public record TeamMemberDto(long teamMemberId,
                            List<TeamRole> roles) {
}
