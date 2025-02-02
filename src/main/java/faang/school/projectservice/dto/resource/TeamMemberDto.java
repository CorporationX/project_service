package faang.school.projectservice.dto.resource;

import faang.school.projectservice.model.TeamRole;
import lombok.Data;

import java.util.List;

@Data
public class TeamMemberDto {
    private Long id;
    private String nickname;
    private List<TeamRole> roles;
}
