package faang.school.projectservice.util.project;

import faang.school.projectservice.exception.ForbiddenException;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProjectUtil {

    public static boolean isAvailable(Project project, Long userId) {
        return project.getVisibility().equals(ProjectVisibility.PUBLIC) ||
                project.getTeams().stream()
                        .flatMap(team -> team.getTeamMembers().stream())
                        .findFirst()
                        .filter(teamMember -> teamMember.getId().equals(userId))
                        .isPresent();
    }
    public static void validateUserInProjectTeam(Long projectId, Long userId, Project project) {
        boolean isUserInProjectTeam = project.getTeams().stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .anyMatch(teamMember -> teamMember.getId().equals(userId));
        if (!isUserInProjectTeam) {
            log.error("Пользователь id = {} не состоит в команде проекта id = {}",
                    userId, projectId);
            throw new ForbiddenException("Пользователь не состоит в команде проекта.");
        }
    }
}
