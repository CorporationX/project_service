package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.UpdateProjectDto;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.ProjectStatus;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ProjectMapper {
    @Mapping(target = "ownerId", source = "requesterId")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "name", expression = "java(createProjectDto.name().trim())")
    @Mapping(target = "description", expression = "java(createProjectDto.description().trim())")
    Project toProject(CreateProjectDto createProjectDto, long requesterId, ProjectStatus status);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateProjectFromDto(UpdateProjectDto dto, @MappingTarget Project project);

    ProjectDto toProjectDto(Project project);
}
