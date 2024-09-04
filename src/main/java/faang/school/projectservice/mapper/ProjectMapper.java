package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
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
                .toList();
    }

    default List<Project> toEntityList(List<ProjectDto> projectsDto) {
        return projectsDto.stream()
                .map(projectDto -> toEntity(projectDto))
                .toList();
    }

    @Named("toListProjectId")
    default List<Long> toListProjectId(List<Project> projects) {
        return projects.stream().map(Project::getId).toList();
    }

    @Named("toListProject")
    default List<Project> toListProject(List<Long> projectIds) {
        return projectIds.stream().map(id -> {
            Project project = new Project();
            project.setId(id);
            return project;
        }).toList();
    }
}
