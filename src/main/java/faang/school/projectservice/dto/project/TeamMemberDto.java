package faang.school.projectservice.dto.project;

import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeamMemberDto {
    private String name;
    private List<TeamRole> roles;
}
