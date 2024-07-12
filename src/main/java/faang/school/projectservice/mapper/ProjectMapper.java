package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    @Mapping(source = "children", target = "childrenProjectIds", qualifiedByName = "mapChildren")
    ProjectDto toDto(Project project);

    @Mapping(source = "parentProjectId", target = "parentProject.id")
    Project toEntity(ProjectDto projectDto);

    @Named("mapChildren")
    default List<Long> mapChildren(List<Project> childrenProjects) {
        return childrenProjects.stream().map(Project::getId).toList();
    }
}
