package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectNameFilter implements ProjectFilter{

    @Override
    public boolean isApplicable(ProjectFilterDto filter) {
        return filter.getNamePattern() != null && filter.getNamePattern().isEmpty();
    }

    @Override
    public void applyFilter(Stream<Project> projectStream, ProjectFilterDto filter) {
        projectStream.filter(project -> project.getName().contains(filter.getNamePattern()));
    }
}
