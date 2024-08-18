package faang.school.projectservice.filter.subprojectfilter;

import faang.school.projectservice.dto.subprojectdto.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class SubProjectNameFilter implements SubProjectFilter{
    @Override
    public boolean isApplicable(SubProjectFilterDto filter) {
        return filter.getName() != null && !filter.getName().isBlank();
    }

    @Override
    public Stream<Project> apply(Stream<Project> subProjects, SubProjectFilterDto filterDto) {
        return subProjects.filter(subProject -> subProject.getName().equals(filterDto.getName()));
    }
}
