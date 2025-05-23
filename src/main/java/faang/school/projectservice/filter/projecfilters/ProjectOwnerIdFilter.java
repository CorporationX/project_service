package faang.school.projectservice.filter.projecfilters;

import faang.school.projectservice.dto.ProjectFilterDto;
import faang.school.projectservice.filter.ProjectFilter;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public class ProjectOwnerIdFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto dto) {
        return dto.getOwnerId() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> projects, ProjectFilterDto dto) {
        return projects
                .filter(project -> project.getOwnerId().equals(dto.getOwnerId()));
    }

}
