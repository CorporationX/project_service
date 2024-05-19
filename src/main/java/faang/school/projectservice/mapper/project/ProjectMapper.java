package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import java.util.List;

import faang.school.projectservice.model.ProjectStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    ProjectDto toDto(Project project);
    Project toProject(ProjectDto projectDto);

    @Mapping(target = "name", source = "name")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Project create(ProjectDto projectDto, String name, String description);

    @Mapping(target = "status", source = "projectStatus")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    Project toProjectForUpdate(ProjectDto projectDto, ProjectStatus projectStatus, String description);

    List<ProjectDto> toDto(List<Project> projects);
}
