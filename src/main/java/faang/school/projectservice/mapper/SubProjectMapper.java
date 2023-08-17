package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.subproject.SubProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SubProjectMapper {
    @Mapping(source = "parentProject.id", target = "parentId")
    @Mapping(source = "children", target = "childrenId", qualifiedByName = "toChildrenId")
    SubProjectDto toDto(Project project);
    void updateFromDto(SubProjectDto subProjectDto, @MappingTarget Project project);

    @Mapping(target = "parentProject", ignore = true)
    @Mapping(target = "children", ignore = true)
    Project toEntity(SubProjectDto subProjectDto);
    @Named("toChildrenId")
    default List<Long> toChildrenId(List<Project> children) {
        return children != null ? children.stream().map(Project::getId).toList() : Collections.emptyList();
    }
}