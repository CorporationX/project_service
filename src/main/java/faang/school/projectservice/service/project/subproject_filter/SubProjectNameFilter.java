package faang.school.projectservice.service.project.subproject_filter;

import faang.school.projectservice.dto.project.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import lombok.Data;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubProjectNameFilter implements SubProjectFilter{
    @Override
    public boolean isApplecable(SubProjectFilterDto filter) {
        return filter.getNamePattern() != null;
    }

    @Override
    public List<Project> apply(List<Project> projects, SubProjectFilterDto filter) {
        return projects.stream()
                .filter(project -> project.getName().contains(filter.getNamePattern()))
                .toList();
    }
}
