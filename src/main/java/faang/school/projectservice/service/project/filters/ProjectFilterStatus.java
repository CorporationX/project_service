package faang.school.projectservice.service.project.filters;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import org.springframework.stereotype.Component;
import java.util.stream.Stream;

@Component
public class ProjectFilterStatus implements ProjectFilter{

    @Override
    public boolean isApplicable(ProjectFilterDto projectFilterDto) {
        return projectFilterDto.getProjectStatus() != null;
    }

    @Override
    public Stream<ProjectDto> filter(Stream<ProjectDto> projectDtoStream, ProjectFilterDto projectFilterDto) {
        return projectDtoStream.filter(projectDto -> projectDto.getStatus().equals(projectFilterDto.getProjectStatus()));
    }
}
