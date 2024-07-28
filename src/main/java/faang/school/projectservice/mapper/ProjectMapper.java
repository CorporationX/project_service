package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    ProjectDto toDto(Project project);

    Project toEntity(ProjectDto projectDto);

    default List<ProjectDto> toDtoList(List<Project> projects) {
        return projects.stream()
                .map(project -> toDto(project))
                .collect(Collectors.toList());
    }

    default List<Project> toEntityList(List<ProjectDto> projectsDto) {
        return projectsDto.stream()
                .map(projectDto -> toEntity(projectDto))
                .collect(Collectors.toList());
    }
}
