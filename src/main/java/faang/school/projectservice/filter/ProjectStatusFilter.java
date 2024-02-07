package faang.school.projectservice.filter;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectStatusFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto projectFilterDto) {
        return projectFilterDto.getStatus() != null;
    }

    @Override
    public Stream<Project> applyFilter(ProjectFilterDto projectFilterDto, Stream<Project> projects) {
        return projects.filter(project -> project.getStatus().equals(projectFilterDto.getStatus()));
    }
}
