package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface MomentMapper {

    Moment toEntity(MomentDto dto);

    @Mapping(target = "resourceIds", source = "resource", qualifiedByName = "resourcesToIds")
    @Mapping(target = "projectIds", source = "projects", qualifiedByName = "projectsToIds")
    @Mapping(target = "filterMonth", ignore = true)
    @Mapping(target = "filterProjectIds", ignore = true)
    MomentDto toDto(Moment entity);

    @Named("projectsToIds")
    default List<Long> projectsToIds(List<Project> projects) {
        if (projects == null || projects.isEmpty()) {
            return Collections.emptyList();
        }
        return projects.stream()
                .map(Project::getId)
                .collect(Collectors.toList());
    }

    @Named("resourcesToIds")
    default List<Long> resourcesToIds(List<Resource> resources) {
        if (resources == null || resources.isEmpty()) {
            return Collections.emptyList();
        }
        return resources.stream()
                .map(Resource::getId)
                .collect(Collectors.toList());
    }
}

