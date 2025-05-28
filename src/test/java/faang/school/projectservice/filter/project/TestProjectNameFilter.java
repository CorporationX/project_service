package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public class TestProjectNameFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto dto) {
        return (dto.getName() != null && !dto.getName().isBlank());
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto dto) {
        return projects.filter(project -> project.getName().equalsIgnoreCase("Bakery"));
    }
}
