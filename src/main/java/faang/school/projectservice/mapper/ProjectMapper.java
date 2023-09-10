package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.project.ProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface  ProjectMapper {

    ProjectMapper INSTANCE = Mappers.getMapper(ProjectMapper.class);
    @Mapping(source = "parentProject.id", target = "parentId")
    @Mapping(source = "children", target = "childrenId", qualifiedByName = "toChildrenId")
    ProjectDto toDto(Project project);

    List<ProjectDto> toDtoList(List<Project> projects);
    void updateFromDto(ProjectDto projectDto, @MappingTarget Project project);

    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "children", ignore = true)
    Project toEntity(ProjectDto projectDto);
    @Named("toChildrenId")
    default List<Long> toChildrenId(List<Project> children) {
        return children != null ? children.stream().map(Project::getId).toList() : Collections.emptyList();
    }
}
