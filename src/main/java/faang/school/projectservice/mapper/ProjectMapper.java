package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(target = "status", constant = "CREATED")
    @Mapping(target = "ownerId", source = "requestUserId")
    @Mapping(target = "visibility", source = "projectDto.visibility", defaultValue = "PUBLIC")
    Project toEntity(ProjectDto projectDto, long requestUserId);

    ProjectDto toDto(Project project);

    Project toEntity(ProjectDto projectDto);

    List<ProjectDto> toDtoList(List<Project> projectList);

    List<Project> toProjectList(List<ProjectDto> projectDtoList);

}
