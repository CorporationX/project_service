package faang.school.projectservice.service.project.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
class ProjectNameFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto filter) {
        return !StringUtils.isBlank(filter.getName());
    }

    @Override
    public Stream<Project> apply(Stream<Project> projectStream, ProjectFilterDto filter) {
        return projectStream.filter(project -> project.getName().equals(filter.getName()));
    }
}
