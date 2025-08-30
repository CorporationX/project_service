package faang.school.projectservice.filter;

import faang.school.projectservice.config.context.UserContext;
import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

/**
 * ProjectFilterName — описание класса.
 * <p>
 * TODO: добавить описание назначения и поведения класса.
 * </p>*
 *
 * @author fuckmynameagain
 * @since 14.08.2025
 */
@Component
@RequiredArgsConstructor
public class ProjectFilterName implements ProjectFilter {
    private final UserContext userContext;

    @Override
    public boolean isApplicable(ProjectDto projectDto) {
        return projectDto.getName() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectDto projectDto) {
        return projects.filter(project -> {
            if (project.getVisibility() == ProjectVisibility.PRIVATE) {
                boolean isMember = project.getTeams().stream()
                        .flatMap(team -> team.getTeamMembers().stream())
                        .anyMatch(user -> user.getId().equals(userContext.getUserId()));
                if (!isMember) {
                    return false;
                }
            }
            return projectDto.getName().equalsIgnoreCase(project.getName());
        });
    }
}