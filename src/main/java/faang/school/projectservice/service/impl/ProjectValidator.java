package faang.school.projectservice.service.impl;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.service.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProjectValidator {

    //private final ProjectRepository projectRepository;
    private final ProjectService projectService;

    void validateUserInProject(Long userId, Long projectId) {
        if (!isUserParticipatedInProject(userId, projectId)) {
            throw new IllegalArgumentException("User with id "
                    + userId + " not in project "
                    + projectId + " at this moment");
        }
    }

    boolean isUserParticipatedInProject(Long userId, Long projectId) {
        Project project = projectService.getProject(projectId);
/*        List<Long> projectUserIds = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .distinct()
                .sorted()
                .toList();*/
        //return projectUserIds.contains(userId);
        return project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId).anyMatch(usrId -> usrId.equals(userId));
    }

    boolean isProjectPublic(Long projectId) {
        Project project = projectService.getProject(projectId);
        return ProjectVisibility.PUBLIC.equals(project.getVisibility());
    }

    //private boolean isUserInProject(Long userId, Long projectId) {
    //        return isUserParticipatedInProject(userId, projectId);
    //}
}
