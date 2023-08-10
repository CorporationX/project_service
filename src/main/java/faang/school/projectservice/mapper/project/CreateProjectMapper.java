package faang.school.projectservice.mapper.project;

import faang.school.projectservice.dto.project.CreateProjectDto;
import faang.school.projectservice.dto.project.ResponseProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CreateProjectMapper {
    CreateProjectMapper INSTANCE = Mappers.getMapper(CreateProjectMapper.class);

    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "children", ignore = true)
    Project toEntity(CreateProjectDto dto);

    @Mapping(target = "parentProjectId", source = "parentProject.id")
    @Mapping(target = "childrenIds", source = "children", qualifiedByName = "childrenToChildrenIds")
    ResponseProjectDto toDto(Project project);

    @Named("childrenToChildrenIds")
    default List<Long> childrenToChildrenIds(List<Project> children) {
        return children.stream()
                .map(Project::getId)
                .collect(Collectors.toList());
    }
}
