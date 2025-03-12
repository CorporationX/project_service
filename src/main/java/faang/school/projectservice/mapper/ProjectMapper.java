package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectDto projectToProjectDto(Project project);

    Project projectDtoToProject(ProjectDto projectDto);

    List<ProjectDto> projectListToProjectDtoList(List<Project> projectList);
    List<Project> projectDtoListToProjectList(List<ProjectDto> projectDtoList);

}
