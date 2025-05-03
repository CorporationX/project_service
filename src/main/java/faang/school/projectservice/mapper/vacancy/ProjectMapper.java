package faang.school.projectservice.mapper.vacancy;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(target = "parentProject", ignore = true)
    Project toEntity(ProjectDto dto);

    @Mapping(target = "parentProjectId", source = "parentProject.id")
    ProjectDto toDto(Project entity);
}
