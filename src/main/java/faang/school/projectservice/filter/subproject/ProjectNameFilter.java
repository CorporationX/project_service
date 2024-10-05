package faang.school.projectservice.filter.subproject;


import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

@Component
public class ProjectNameFilter implements ProjectFilter {
    public boolean isApplicable(ProjectFilterDto projectFilterDto) {
        return projectFilterDto.name() != null;
    }

    public Stream<Project> filter(Stream<Project> projectStream, ProjectFilterDto projectFilterDto) {
        if (isApplicable(projectFilterDto)) {
            return projectStream.filter(project -> project.getName().contains(projectFilterDto.name()));
        }
        return projectStream;
    }
}
