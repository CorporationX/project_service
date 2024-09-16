package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.CreateSubProjectDto;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProjectMapper {

    @Mapping(source = "parentProject.id", target = "parentProjectId")
    @Mapping(source = "children", target = "children", qualifiedByName = "mapChildrenToIds")
    CreateSubProjectDto toDTO(Project project);

    @Mapping(source = "parentProjectId", target = "parentProject.id")
    @Mapping(target = "children", ignore = true)
    Project toEntity(CreateSubProjectDto projectDTO);

    @Named("mapChildrenToIds")
    default List<Long> mapChildrenToIds(List<Project> children) {
        return children != null ? children.stream()
                .map(Project::getId)
                .collect(Collectors.toList())
                : new ArrayList<>();
    }
}
