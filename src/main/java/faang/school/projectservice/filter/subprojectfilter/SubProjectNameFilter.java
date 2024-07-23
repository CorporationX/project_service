package faang.school.projectservice.filter.subprojectfilter;

import faang.school.projectservice.dto.SubProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public class SubProjectNameFilter implements SubProjectFilter{
    @Override
    public boolean isApplicable(SubProjectFilterDto filter) {
        return filter.getName() != null && !filter.getName().isBlank();
    }

    @Override
    public Stream<Project> apply(Stream<Project> subProjects, SubProjectFilterDto filterDto) {
        return subProjects.filter(subProject -> subProject.getName().contains(filterDto.getName()));
    }
}
