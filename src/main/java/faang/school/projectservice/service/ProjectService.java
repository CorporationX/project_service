package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;

    public boolean checkOwnerPermission(Long userId, Long projectId) {
        Project project = getProjectById(projectId);
        return projectRepository.existsByOwnerUserIdAndName(userId, project.getName());
    }

    public boolean checkManagerPermission(Long userId, Long projectId) {
        Project project = getProjectById(projectId);
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .filter(teamMember -> teamMember.getUserId().equals(userId))
                .anyMatch(teamMember -> teamMember.getRoles().contains(TeamRole.MANAGER));
    }

    public Project getProjectById(Long projectId) {
        return projectRepository.getProjectById(projectId);
    }
}
