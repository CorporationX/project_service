package faang.school.projectservice.filter.subprojectfilter;

import faang.school.projectservice.dto.SubProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public class SubProjectStatusFilter implements SubProjectFilter{
    @Override
    public boolean isApplicable(SubProjectFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> subProjects, SubProjectFilterDto filter) {
        return subProjects.filter(subProject -> subProject.getStatus().equals(filter.getStatus()));
    }
}
