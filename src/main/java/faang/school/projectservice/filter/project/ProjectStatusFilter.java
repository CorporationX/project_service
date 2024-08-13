package faang.school.projectservice.filter.project;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectStatusFilter implements ProjectFilter {
    public boolean isApplicable(ProjectFilterDto projectFilterDto) {
        return projectFilterDto.getProjectStatus() != null;
    }

    public Stream<Project> filter(Stream<Project> projectStream, ProjectFilterDto projectFilterDto) {
        if (isApplicable(projectFilterDto)) {
            return projectStream.filter(project -> project.getStatus() == projectFilterDto.getProjectStatus());
        }
        return projectStream;
    }
}
