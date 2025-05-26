package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ProjectMapper {

    @Mapping(target = "parentProjectId", source = "parentProject", qualifiedByName = "projectToId")
    @Mapping(target = "childrenIds", source = "children", qualifiedByName = "projectsToIds")
    ProjectDto toDto(Project project);

    @Mapping(target = "parentProject", source = "parenProjectId", qualifiedByName = "idToProject")
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    @Mapping(target = "resources", ignore = true)
    @Mapping(target = "teams", ignore = true)
    @Mapping(target = "schedule", ignore = true)
    @Mapping(target = "stages", ignore = true)
    @Mapping(target = "vacancies", ignore = true)
    @Mapping(target = "moments", ignore = true)
    @Mapping(target = "meets", ignore = true)
    Project toProject(ProjectDto projectDto);

    @Named("projectToId")
    static Long projectToId(Project project) {
        return project != null ? project.getId() : null;
    }

    @Named("projectsToIds")
    static List<Long> projectsToIds(List<Project> projects) {
        return projects != null ?
                projects.stream()
                        .map(Project::getId)
                        .collect(Collectors.toList()) :
                null;
    }

    @Named("idToProject")
    default Project idToProject(Long id) {
        if (id == null) {
            return null;
        }
        Project project = new Project();
        project.setId(id);
        return project;
    }
}