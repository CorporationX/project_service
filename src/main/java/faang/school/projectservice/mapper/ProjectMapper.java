package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {
    @Mapping(target = "parentProject", source = "parentProjectId")
    @Mapping(target = "children", source = "childrenIds", qualifiedByName = "idEntityList")
    Project toProject(ProjectDto projectDto);

    @Named("idEntityList")
    default List<Project> idEntityList(List<Long> idList) {
        return idList != null ?
                idList.stream()
                        .map(id -> Project.builder()
                                .id(id).build()).toList() : null;
    }

    default Project map(Long parentProjectId) {
        if (parentProjectId == null) {
            return null;
        }
        Project project = new Project();
        project.setId(parentProjectId);
        return project;
    }

    @Mapping(target = "parentProjectId", source = "parentProject.id")
    @Mapping(target = "childrenIds", source = "children", qualifiedByName = "idList")
    ProjectDto toProjectDto(Project project);

    @Named("idList")
    default List<Long> idList(List<Project> projects) {
        return projects != null ? projects.stream().map(Project::getId).toList() : null;
    }

    List<ProjectDto> toListProjectDto(List<Project> projects);

    List<Project> toListProject(List<ProjectDto> projectDtoList);
}
