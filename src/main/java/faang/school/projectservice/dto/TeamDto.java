package faang.school.projectservice.dto;

import faang.school.projectservice.model.Project;
import lombok.Data;

import java.util.List;

@Data
public class TeamDto {
    private Long id;
    private List<TeamMemberDto> teamMembers;
    private Project project;
}
