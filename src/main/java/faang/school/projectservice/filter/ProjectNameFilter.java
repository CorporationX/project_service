package faang.school.projectservice.filter;

import faang.school.projectservice.dto.project.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Stream;

@Component
public class ProjectNameFilter implements ProjectFilter {
    @Override
    public boolean isApplicable(ProjectFilterDto projectFilterDto) {
        return projectFilterDto.getNamePattern() != null;
    }

    @Override
    public Stream<Project> apply(List<Project> projectList, ProjectFilterDto projectFilterDto) {
        return projectList.stream().filter(project -> project.getName().equals(projectFilterDto.getNamePattern()));
    }
}
