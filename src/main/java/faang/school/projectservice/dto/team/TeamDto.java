package faang.school.projectservice.dto.team;


import faang.school.projectservice.dto.project.ProjectDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamDto {
    private Long id;
    private List<TeamMemberDto> teamMembers;
    private ProjectDto project;
}
