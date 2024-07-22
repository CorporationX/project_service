package faang.school.projectservice.mapper;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InitiativeMapperHelper {
    private final TeamMemberRepository teamMemberRepository;
    private final ProjectRepository projectRepository;

    public TeamMember mapTeamMember(Long teamMemberId){
        return teamMemberRepository.findById(teamMemberId);
    }

    public Project mapProject(Long projectId){
        return projectRepository.getProjectById(projectId);
    }
}
