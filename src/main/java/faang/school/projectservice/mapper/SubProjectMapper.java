package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.sub_project.SubProjectCreateDto;
import faang.school.projectservice.dto.sub_project.SubProjectUpdateDto;
import faang.school.projectservice.dto.sub_project.SubProjectViewDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

/**
 * SubProjectMapper — маппер для преобразования между сущностью Project и DTO подпроектов
 *
 * @author Linempy
 * @since 21.07.2025
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubProjectMapper {

    Project toEntity(SubProjectCreateDto createDto);

    @Mapping(source = "children", target = "childrenId", qualifiedByName = "mapChildrenToIds")
    @Mapping(source = "parentProject", target = "parentId", qualifiedByName = "mapToParentId")
    SubProjectViewDto toViewDto(Project project);

    Project update(@MappingTarget Project project, SubProjectUpdateDto updateDto);

    @Named("mapChildrenToIds")
    default List<Long> mapChildrenToIds(List<Project> children) {
        if (children == null) {
            return List.of();
        }
        return children.stream()
                .map(Project::getId)
                .toList();
    }

    @Named("mapToParentId")
    default Long mapToParentId(Project parent) {
        if (parent == null) {
            return null;
        }

        return parent.getId();
    }
}