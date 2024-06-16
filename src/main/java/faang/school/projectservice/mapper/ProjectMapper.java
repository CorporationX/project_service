
package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProjectMapper {
    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "children", ignore = true)
    Project toModel(ProjectDto project);

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    @Mapping(source = "children", target = "children", qualifiedByName = "mapSubprojects")
    ProjectDto toDto(Project project);

    List<Project> toModelList(List<ProjectDto> project);

    List<ProjectDto> toDto(List<Project> project);

    @Named("mapSubprojects")
    default List<Long> mapSubprojects(List<Project> children) {
        if (children == null) {
            return List.of();
        }

        return children.stream()
                .map(Project::getId)
                .toList();
    }
}
