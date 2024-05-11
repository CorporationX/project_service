package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectPrivacyFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto filter) {
        return true;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projectStream, ProjectFilterDto filter) {
        return projectStream.filter(project -> visibleToMember(project, filter.getUserId()));
    }

    private boolean visibleToMember(Project project, Long userId) {
        if (project.getVisibility() == ProjectVisibility.PUBLIC) {
            return true;
        }
        return project.getTeams()
                .stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
                .anyMatch(memberId -> memberId.equals(userId));
    }
}
