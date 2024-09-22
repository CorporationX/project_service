package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectDto toProjectDto(Project project);

    Project toProject(ProjectDto projectDto);

    List<ProjectDto> toProjectDtos(List<Project> projects);
}
