package faang.school.projectservice.filter.projecfilters;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public class TestProjectNameFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectDto dto) {
        return (dto.getName() != null && !dto.getName().isBlank());
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectDto dto) {
        return projects.filter(project -> project.getName().equalsIgnoreCase("Bakery"));
    }
}
