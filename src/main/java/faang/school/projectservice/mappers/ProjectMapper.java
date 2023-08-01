package faang.school.projectservice.mappers;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    Project toEntity(ProjectDto projectDto);

    ProjectDto toDto(Project project);

    void updateProjectFromDto(ProjectDto projectDto, @MappingTarget Project project);

    List<ProjectDto> toDtoList(List<Project> projects);
}
