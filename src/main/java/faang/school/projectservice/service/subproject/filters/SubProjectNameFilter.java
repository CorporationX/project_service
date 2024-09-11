package faang.school.projectservice.service.subproject.filters;

import faang.school.projectservice.dto.client.subproject.SubProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class SubProjectNameFilter implements SubProjectFilter {
    @Override
    public boolean isApplicable(SubProjectFilterDto subProjectFilterDto) {
        return subProjectFilterDto.getName() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, SubProjectFilterDto subProjectFilterDto) {
        return projects
                .filter(project -> project.getName().contains(subProjectFilterDto.getName()));
    }
}
