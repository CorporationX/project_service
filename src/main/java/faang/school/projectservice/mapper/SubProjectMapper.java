package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.subproject.CreateSubProjectDto;
import faang.school.projectservice.dto.subproject.SubProjectResponseDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubProjectMapper {

    @Mapping(target = "children", ignore = true)
    Project toProjectEntity(CreateSubProjectDto dto);

    SubProjectResponseDto toSubProjectResponseDto(Project entity);

    List<SubProjectResponseDto> toSubProjectResponseDtos(List<Project> entities);

    @AfterMapping
    default void afterMapping(CreateSubProjectDto dto, @MappingTarget Project project) {
        if (dto.subProjectIds() != null) {
            project.setChildren(mapSubProjectIdsToProjects(dto.subProjectIds()));
        }
    }

    default List<Project> mapSubProjectIdsToProjects(List<Long> subProjectIds) {
        if (subProjectIds == null) {
            return Collections.emptyList();
        }
        return subProjectIds.stream()
                .map(id -> {
                    Project project = new Project();
                    project.setId(id);
                    return project;
                })
                .collect(Collectors.toList());
    }
}

