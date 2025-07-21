package faang.school.projectservice.service.filter.project;

import faang.school.projectservice.dto.client.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.filter.Filter;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectNameFilter implements Filter<Project, ProjectFilterDto> {
    @Override
    public boolean isApplicable(ProjectFilterDto dto) {
        return dto.name() != null;
    }

    @Override
    public Stream<Project> filter(Stream<Project> entities, ProjectFilterDto dto) {
        return entities
                .filter(project -> project.getName().equals(dto.name()));
    }
}
