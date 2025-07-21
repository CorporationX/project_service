package faang.school.projectservice.service.filter.project;

import faang.school.projectservice.dto.client.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.service.filter.Filter;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectStatusFilter implements Filter<Project, ProjectFilterDto> {
    @Override
    public boolean isApplicable(ProjectFilterDto dto) {
        return dto.status() != null;
    }

    @Override
    public Stream<Project> filter(Stream<Project> entities, ProjectFilterDto dto) {
        return entities
                .filter(project -> project.getStatus().equals(dto.status()));
    }
}
