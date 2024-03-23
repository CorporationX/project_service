package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.filter.ProjectFilterDto;
import faang.school.projectservice.dto.project.ProjectDtoFilter;
import faang.school.projectservice.model.Project;

import java.util.List;
import java.util.stream.Stream;

public interface ProjectFilter {
    boolean isApplicable(ProjectDtoFilter projectDtoFilter);

    boolean isApplicable(ProjectFilterDto filterDto);

    Stream<Project> apply(Stream<Project> projects, ProjectFilterDto filterDto);

    void apply(List<Project> projects, ProjectDtoFilter projectDtoFilter);
}
