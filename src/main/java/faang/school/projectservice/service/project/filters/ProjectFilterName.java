package faang.school.projectservice.service.project.filters;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectFilterDto;
import org.springframework.stereotype.Component;
import java.util.stream.Stream;

@Component
public class ProjectFilterName implements ProjectFilter{

    @Override
    public boolean isApplicable(ProjectFilterDto projectFilterDto) {
        return projectFilterDto.getName() != null && !projectFilterDto.getName().isBlank();
    }

    @Override
    public Stream<ProjectDto> filter(Stream<ProjectDto> projectDtoStream, ProjectFilterDto projectFilterDto) {
        return projectDtoStream.filter(projectDto -> projectDto.getName().equals(projectFilterDto.getName()));
    }
}
