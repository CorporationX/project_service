package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(source = "parentProject", target = "parentId", qualifiedByName = "toParentId")
    @Mapping(source = "children", target = "childrenId", qualifiedByName = "toChildrenId")
    ProjectDto toDto(Project project);

    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "children", ignore = true)
    Project toEntity(ProjectDto projectDto);

    @Named("toParentId")
    default Long toParentId(Project parentProject) {
        return parentProject.getId();
    }

    @Named("toChildrenId")
    default List<Long> toChildrenId(List<Project> children) {
        return children != null ? children.stream().map(Project::getId).toList() : Collections.emptyList();
    }
}
