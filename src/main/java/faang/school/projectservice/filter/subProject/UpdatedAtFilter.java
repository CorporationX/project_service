package faang.school.projectservice.filter.subProject;

import faang.school.projectservice.dto.project.FilterProjectDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class UpdatedAtFilter implements Filter<FilterProjectDto, Project> {
    @Override
    public boolean isApplicable(FilterProjectDto filterDto) {
        return filterDto.getUpdatedAt() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> itemStream, FilterProjectDto filterDto) {
        return itemStream.filter(project -> project.getUpdatedAt().equals(filterDto.getUpdatedAt()));
    }
}
