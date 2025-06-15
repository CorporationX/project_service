package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectDescriptionFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto dto) {
        return dto.getDescriptionPattern() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto dto) {
        String lowerCaseDescriptionPattern = dto.getDescriptionPattern().toLowerCase();
        return projects.filter(project ->
                        project.getDescription().toLowerCase().contains(lowerCaseDescriptionPattern));
    }
}
