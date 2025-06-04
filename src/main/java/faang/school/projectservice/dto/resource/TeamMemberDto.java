package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.TeamRole;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class TeamMemberDto {
    private Long userId;
    private String nickname;
    private List<TeamRole> roles;
}
