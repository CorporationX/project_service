package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    ProjectDto toDto(Project project);
    Project toEntity(ProjectDto projectDto);
    void updateFromDto(ProjectDto projectDto, @MappingTarget Project project);
}
