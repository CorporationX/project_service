package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    @Mapping(source = "ownerId", target = "ownerId")
    ProjectDto toDto(Project project);
    Project toEntity(ProjectDto projectDto);
    List<ProjectDto> toDtoList(List<Project> projects);
}
