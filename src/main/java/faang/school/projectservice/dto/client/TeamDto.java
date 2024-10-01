package faang.school.projectservice.dto.client;

import faang.school.projectservice.dto.project.ProjectDto;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class TeamDto {
    private Long id;
    private List<TeamMemberDto> teamMembers;
    private ProjectDto project;
}
