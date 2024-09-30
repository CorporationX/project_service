package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(target = "id", source = "dto.id")
    @Mapping(target = "name", source = "dto.name")
    @Mapping(target = "description", source = "dto.description")
    @Mapping(target = "status", defaultValue = "CREATED")
    @Mapping(target = "visibility", source = "parentProject.visibility")
    Project toEntity(CreateSubProjectDto dto, Project parentProject, Long ownerId);

    @Mapping(target = "children", source = "children", qualifiedByName = "subProjects")
    ProjectDto toDto(Project project);

    List<ProjectDto> toDtos(List<Project> projects);

    @Named("subProjects")
    default List<ProjectDto> subProjects(List<Project> projects) {
        return projects.stream()
                .map(this::toDtoWithoutSubprojects)
                .toList();
    }

    @Named("withoutSubprojects")
    @Mapping(target = "children", defaultExpression = "java(java.util.List.of()")
    ProjectDto toDtoWithoutSubprojects(Project project);
}