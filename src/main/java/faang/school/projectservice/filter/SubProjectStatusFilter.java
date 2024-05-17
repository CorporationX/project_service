package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.SubProjectFilterDto;

import java.util.List;
import java.util.Objects;

public class SubProjectStatusFilter implements SubProjectFilter {
    @Override
    public boolean isApplicable(SubProjectFilterDto filter) {
        return Objects.nonNull(filter.getStatus());
    }

    @Override
    public void apply(List<ProjectDto> projects, SubProjectFilterDto filter) {
        projects.removeIf(project -> !project.getStatus().equals(filter.getStatus()));
    }
}
