package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class ProjectVisibilityFilterImpl implements ProjectVisibilityFilter{
    public boolean isVisible(Project project, Long userId) {
        if (project.getVisibility() == ProjectVisibility.PUBLIC) {
            return true;
        }
        return project.getTeams()
                .stream()
                .map(Team::getTeamMembers)
                .flatMap(Collection::stream)
                .map(TeamMember::getId)
                .anyMatch(userId::equals);
    }
}
