package faang.school.projectservice.filter.subproject;

import faang.school.projectservice.dto.SubProjectsFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class SubProjectNameFilter implements SubProjectFilter {
    @Override
    public boolean isApplicable(SubProjectsFilterDto subProjectsFilterDto) {
        return subProjectsFilterDto.name() != null && !subProjectsFilterDto.name().isBlank();
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, SubProjectsFilterDto subProjectsFilterDto) {
        return projects
                .filter(this::isPublic)
                .filter(project -> project.getName().equalsIgnoreCase(subProjectsFilterDto.name()));
    }
}
