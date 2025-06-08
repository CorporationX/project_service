package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring" , unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);

    @Mapping(target = "status", expression = "java(project.getStatus().name())")
    ProjectDto toDto(Project project);

    @Mapping(target = "status", expression = "java(faang.school.projectservice.model.ProjectStatus.valueOf(projectDto.getStatus()))")
    Project toEntity(ProjectDto projectDto);
}