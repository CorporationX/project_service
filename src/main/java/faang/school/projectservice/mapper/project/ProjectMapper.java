package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    Project toModel(ProjectDto project);

    ProjectDto toDto(Project project);

    List<Project> toModel(List<ProjectDto> project);

    List<ProjectDto> toDto(List<Project> project);
}