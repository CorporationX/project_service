package faang.school.projectservice.filter.subProjectFilter;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class SubProjectNameFilter implements SubProjectFilter {
    @Override
    public boolean isApplicable(SubProjectFilterDto filter) {
        return Objects.nonNull(filter.getName());
    }

    @Override
    public void apply(List<ProjectDto> projects, SubProjectFilterDto filter) {
        projects.removeIf(project -> !project.getName().contains(filter.getName()));
    }
}
