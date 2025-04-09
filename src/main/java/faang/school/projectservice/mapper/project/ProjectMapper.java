package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    ProjectDto projectToProjectDto(Project project);

    Project projectDtoToProject(ProjectDto projectDto);

    List<ProjectDto> projectListToProjectDtoList(List<Project> projectList);
}
