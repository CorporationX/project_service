package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectCreateDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProjectMapper {

    ProjectCreateDto toDto(Project project);

    Project toModel(ProjectCreateDto projectCreateDto);
}