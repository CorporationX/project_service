package faang.school.projectservice.filter.projecfilters;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public class ProjectDescriptionFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto dto) {
        return dto.getDescriptionPattern() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto dto) {
        return projects
                .filter(project -> project.getDescription().toLowerCase().contains(dto.getDescriptionPattern()));
    }
}
