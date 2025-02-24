package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProjectMapper {

    ProjectDto toDto(Project project);

    Project toEntity(ProjectDto dto);

    @Mapping(target = "id", ignore = true)
    Project update(ProjectDto dto, @MappingTarget Project project);
}
