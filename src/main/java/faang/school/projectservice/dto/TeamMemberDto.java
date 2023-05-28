package faang.school.projectservice.dto;

import faang.school.projectservice.model.TeamRole;
import lombok.Data;

import java.util.List;

@Data
public class TeamMemberDto {
    private Long id;
    private Long userId;
    private List<TeamRole> roles;
    private TeamDto team;
}
