package faang.school.projectservice.mapper.project;

import java.util.List;

import org.mapstruct.Mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    Project toModel(ProjectDto project);
    
    ProjectDto toDto(Project project);
    
    List<Project> toModelList(List<ProjectDto> project);
    
    List<ProjectDto> toDto(List<Project> project);
}