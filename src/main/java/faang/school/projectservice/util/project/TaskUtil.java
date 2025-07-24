package faang.school.projectservice.util.project;

import faang.school.projectservice.model.Project;

public class TaskUtil {
    public static boolean isInTeam(Project project, Long userId) {
        return project.getTeams().stream()
                        .flatMap(team -> team.getTeamMembers().stream())
                        .findFirst()
                        .filter(teamMember -> teamMember.getId() == userId)
                        .isPresent();
    }
}
