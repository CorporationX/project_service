package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.CreateSubProjectDto;
import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD)
public interface ProjectMapper {
    @Mapping(source = "parentProject.id", target = "parentProjectId")
//    @Mapping(source = "children", target = "children", qualifiedByName = "mapToLongChildrenId")
    ProjectDto toDto(Project project);

    Project toEntity(CreateSubProjectDto createSubProjectDto);

    Project toProject(ProjectDto project);

    List<ProjectDto> toDtos(List<Project> projects);

    @BeanMapping(ignoreByDefault = true,
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(source = "status", target = "status")
    @Mapping(source = "description", target = "description")
    void updateProject(ProjectDto projectDto, @MappingTarget Project project);

    default List<Long> mapToLongChildrenId(List<Project> children) {
        if (children != null) {
            return children.stream()
                    .map(Project::getId)
                    .toList();
        }
        return Collections.emptyList();
    }

    default List<Project> mapIdsToProjects(List<Long> childrenIds) {
        if (childrenIds == null) {
            return Collections.emptyList();
        }
        return childrenIds.stream()
                .map(id -> Project.builder()
                        .id(id)
                        .build())
                .toList();
    }

}