package faang.school.projectservice.filter.subProject;

import faang.school.projectservice.dto.project.FilterDto;
import faang.school.projectservice.filter.Filter;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectVisibility;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;
@Component
public class VisibilityFilter implements Filter<FilterDto, Project> {
    @Override
    public boolean isApplicable(FilterDto filterDto) {
        boolean applicable = false;
        if (filterDto.getVisibility() != null){
            if (filterDto.getVisibility()!=ProjectVisibility.PRIVATE){
                applicable = true;
            }
        }
        return applicable;
    }

    @Override
    public Stream<Project> apply(Stream<Project> itemStream, FilterDto filterDto) {
        return itemStream.filter(project -> project.getVisibility().equals(filterDto.getVisibility()));
    }
}
