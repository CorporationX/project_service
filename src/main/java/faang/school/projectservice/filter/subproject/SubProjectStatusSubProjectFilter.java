package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.subproject.FilterSubProjectDto;
import faang.school.projectservice.filter.SubProjectFilter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class SubProjectStatusSubProjectFilter implements SubProjectFilter<FilterSubProjectDto, Project> {
    @Override
    public boolean isApplicable(FilterSubProjectDto filterDto) {
        return filterDto.getStatus() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> itemStream, FilterSubProjectDto filterDto) {
        return itemStream.filter(project -> project.getStatus().equals(filterDto.getStatus()));
    }
}
