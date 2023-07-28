package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(source = "parentProject.id", target = "parentId")
    @Mapping(source = "children", target = "childrenId", qualifiedByName = "toChildrenId")
    ProjectDto toDto(Project project);

    List<ProjectDto> toDtoList(List<Project> projects);
    void updateFromDto(ProjectDto projectDto, @MappingTarget Project project);

    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "children", ignore = true)
    Project toEntity(ProjectDto projectDto);

    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "children", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateFromDto(ProjectDto projectDto, @MappingTarget Project project);

    @Named("toChildrenId")
    default List<Long> toChildrenId(List<Project> children) {
        return children != null ? children.stream().map(Project::getId).toList() : Collections.emptyList();
    }
}
