package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.dto.project.ProjectUpdateDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    @Mapping(source = "children", target = "childrenProjectIds", qualifiedByName = "mapChildren")
    ProjectDto toDto(Project project);

    Project toEntity(ProjectDto projectDto);

    Project update(ProjectUpdateDto projectUpdateDto, @MappingTarget Project project);

    @Named("mapChildren")
    default List<Long> mapChildren(List<Project> childrenProjects) {
        if (childrenProjects == null || childrenProjects.isEmpty()) {
            return Collections.emptyList();
        }
        return childrenProjects.stream().map(Project::getId).toList();
    }
}
