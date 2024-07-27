package faang.school.projectservice.service.project.subproject_filter;

import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubProjectStatusFilter implements SubProjectFilter{
    @Override
    public boolean isApplecable(SubProjectFilterDto filter) {
        return filter.getStatus() != null;
    }

    @Override
    public List<Project> apply(List<Project> projects, SubProjectFilterDto filter) {
        return projects.stream()
                .filter(project -> project.getStatus() == filter.getStatus())
                .toList();
    }
}
