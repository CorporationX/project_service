package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectNameFilter implements ProjectFilter {

    @Override
    public boolean isApplicable(ProjectFilterDto filterDto) {
        return filterDto.getName() != null &&
                !filterDto.getName().trim().isEmpty();
    }

    @Override
    public Stream<Project> apply(Stream<Project> stream, ProjectFilterDto filterDto) {
        return stream.filter(project -> project.getName().toLowerCase()
                .contains(filterDto.getName().trim().toLowerCase()));
    }

}
