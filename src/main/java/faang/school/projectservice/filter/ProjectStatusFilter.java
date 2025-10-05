package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;

import java.util.stream.Stream;

public class ProjectStatusFilter implements ProjectFilter{
    @Override
    public boolean isApplicable(ProjectFilterDto filterDto) {
        return filterDto != null && filterDto.getStatus() != null;
    }

    @Override
    public Stream<Project> apply(Stream<Project> stream, ProjectFilterDto filterDto) {
        if (stream == null || filterDto == null || filterDto.getStatus() == null) {
            return Stream.empty();
        }
        return stream.filter(project -> filterDto.getStatus().equals(project.getStatus()));
    }
}
