package faang.school.projectservice.filter.subproject;


import faang.school.projectservice.dto.subproject.ProjectFilterDto;
import org.springframework.stereotype.Component;
import faang.school.projectservice.model.Project;

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