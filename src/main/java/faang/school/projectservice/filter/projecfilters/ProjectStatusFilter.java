package faang.school.projectservice.filter.projecfilters;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.filter.ProjectFilter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProjectStatusFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectDto dto) {
        return dto.getStatus() != null;
    }

    @Override
    public List<ProjectDto> apply(ProjectDto dto) {
        return List.of();
    }
}
