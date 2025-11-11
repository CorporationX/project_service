package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class FilterProjectStatus implements FilterProject{
    @Override
    public boolean isApplication(ProjectFilterDto projectFilterDto) {
        return projectFilterDto.status() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto projectFilterDto) {
        return projects.filter(project -> project.getStatus().equals(projectFilterDto.status()));
    }
}
