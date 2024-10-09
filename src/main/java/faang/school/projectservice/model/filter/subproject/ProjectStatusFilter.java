package faang.school.projectservice.model.filter.subproject;


import faang.school.projectservice.model.dto.project.ProjectFilterDto;
import org.springframework.stereotype.Component;
import faang.school.projectservice.model.entity.Project;

import java.util.stream.Stream;

@Component
public class ProjectStatusFilter implements ProjectFilter {
    public boolean isApplicable(ProjectFilterDto projectFilterDto) {
        return projectFilterDto.projectStatus() != null;
    }

    public Stream<Project> filter(Stream<Project> projectStream, ProjectFilterDto projectFilterDto) {
        if (isApplicable(projectFilterDto)) {
            return projectStream.filter(project -> project.getStatus() == projectFilterDto.projectStatus());
        }
        return projectStream;
    }
}