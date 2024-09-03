package faang.school.projectservice.mapper.team;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.TeamMemberRepository;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamMapperHelper {
    private final ProjectService projectService;
    private final TeamMemberRepository teamMemberRepository;

    public Project findProjectById(Long projectId){
        return projectService.getProjectById(projectId);
    }

    public List<TeamMember> findTeamMembers(List<Long> teamMembers){
        return teamMemberRepository.findByUserIds(teamMembers);
    }
}
