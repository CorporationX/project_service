package faang.school.projectservice.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE, 
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    Project toEntity(ProjectDto projectDto);
    ProjectDto toDto(Project project);
    void update(@MappingTarget Project project, ProjectDto projectDto);
    List<Project> toEntities(List<ProjectDto> dtos);
    List<ProjectDto> toDtos(List<Project> projects);
}
