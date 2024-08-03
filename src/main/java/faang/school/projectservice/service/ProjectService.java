package faang.school.projectservice.service;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.repository.ProjectRepository;
import faang.school.projectservice.repository.TeamMemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProjectService {
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    public List<Project> findDifferentProjects(List<Project> projectsFromDataBase, List<Long> newProjectIds) {
        List<Long> existingProjectIds = projectsFromDataBase.stream()
                .map(Project::getId)
                .toList();
        newProjectIds.removeAll(existingProjectIds);
        return convertProjectsByIds(newProjectIds);
    }

    public List<Project> getNewProjects(List<Long> userIds) {
        Set<Project> projects = new HashSet<>();
        userIds.forEach(userId -> {
            List<TeamMember> teamMembers = teamMemberRepository.findByUserId(userId);
            teamMembers.forEach(teamMember -> projects.add(teamMember.getTeam().getProject()));
        });
        return new ArrayList<>(projects);
    }

    private List<Project> convertProjectsByIds(List<Long> projectIds) {
        return projectIds.stream()
                .map(projectRepository::getProjectById)
                .toList();
    }
}
