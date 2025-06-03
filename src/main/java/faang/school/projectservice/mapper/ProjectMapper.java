package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    Project toProject(ProjectDto projectDto);

    ProjectDto toProjectDto(Project project);

    List<ProjectDto> toProjectDtoList(List<Project> projects);
}