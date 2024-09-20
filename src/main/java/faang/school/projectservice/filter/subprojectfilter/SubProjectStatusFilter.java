package faang.school.projectservice.filter.subprojectfilter;

import faang.school.projectservice.dto.subprojectdto.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
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