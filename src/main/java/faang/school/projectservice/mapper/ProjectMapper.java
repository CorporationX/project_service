package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectRequestDto;
import faang.school.projectservice.dto.ProjectResponseDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    Project projectRequestDtoToEntity(ProjectRequestDto dto);

    ProjectResponseDto projectEntityToProjectResponseDto(Project entity);

    List<ProjectResponseDto> projectEntitiesToProjectResponseDtos(List<Project> entities);
}
