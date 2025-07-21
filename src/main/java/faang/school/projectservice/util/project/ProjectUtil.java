package faang.school.projectservice.util.project;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;

public class ProjectUtil {
    public static boolean isAvailable(Project project, Long userId) {
        return project.getVisibility().equals(ProjectVisibility.PUBLIC) ||
                project.getTeams().stream()
                        .flatMap(team -> team.getTeamMembers().stream())
                        .findFirst()
                        .filter(teamMember -> teamMember.getId() == userId)
                        .isPresent();
    }
}
