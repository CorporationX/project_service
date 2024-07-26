package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.filter.ProjectFilterDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface ProjectMapper {
    ProjectDto toDto(Project project);

    Project toEntity(ProjectDto projectDto);

    ProjectFilterDto toFilter(ProjectDto projectDto);
}
