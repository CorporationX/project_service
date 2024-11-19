package faang.school.projectservice.filter.subproject;

import com.amazonaws.util.StringUtils;
import faang.school.projectservice.dto.subproject.FilterSubProjectDto;
import faang.school.projectservice.filter.SubProjectFilter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class SubProjectNameSubProjectFilter implements SubProjectFilter<FilterSubProjectDto, Project> {
    @Override
    public boolean isApplicable(FilterSubProjectDto filterDto) {
        return !StringUtils.isNullOrEmpty(filterDto.getName());
    }

    @Override
    public Stream<Project> apply(Stream<Project> itemStream, FilterSubProjectDto filterDto) {
        return itemStream.filter(project -> project.getName().contains(filterDto.getName()));
    }
}
