package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.stream.Stream;

@Component
public class FilterProjectName implements FilterProject{
    @Override
    public boolean isApplication(ProjectFilterDto projectFilterDto) {
        return projectFilterDto.name() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto projectFilterDto) {
        return projects.filter(project ->
                project.getName().contains(Objects.requireNonNull(projectFilterDto.name())));
    }
}
