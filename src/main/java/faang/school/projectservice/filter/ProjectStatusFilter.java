package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import faang.school.projectservice.model.TeamMember;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectStatusFilter implements ProjectFilter {

    @Override
    public boolean isApplicable(ProjectFilterDto filterDto) {
        return filterDto.getProjectStatus() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projectStream, ProjectFilterDto filterDto) {
        Stream<Project> filteredProjectStream = projectStream.filter(project -> project.getStatus().toString().equalsIgnoreCase(filterDto.getProjectStatus()));
        return filteredProjectStream.filter(project -> filterByVisibility(project, filterDto));
    }

    @Override
    public boolean filterByVisibility(Project project, ProjectFilterDto filterDto) {
        if (project.getVisibility() == ProjectVisibility.PRIVATE) {
            return project.getTeams().stream()
                    .flatMap(team -> team.getTeamMembers().stream()
                            .map(TeamMember::getId))
                    .toList().contains(filterDto.getTeamMemberId());
        }
        return true;
    }
}
