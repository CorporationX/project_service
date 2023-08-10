package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectNameFilter implements ProjectFilter{
    @Override
    public boolean isApplicable(ProjectFilterDto filter) {
        return filter.getName()!=null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filter) {
        return projects.filter(project -> project.getName().equals(filter.getName()));
    }
}
