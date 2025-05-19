package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public interface MomentMapper {
    @Mapping(target = "resource", source = "resourceIds", qualifiedByName = "idsToResources")
    @Mapping(target = "projects", source = "projectIds", qualifiedByName = "idsToProjects")
    Moment toEntity(MomentDto dto);

    @Mapping(target = "resourceIds", source = "resource", qualifiedByName = "resourcesToIds")
    @Mapping(target = "projectIds", source = "projects", qualifiedByName = "projectsToIds")
    @Mapping(target = "projectId", ignore = true)
    @Mapping(target = "filterMonth", ignore = true)
    @Mapping(target = "filterProjectIds", ignore = true)
    MomentDto toDto(Moment entity);

    List<MomentDto> toDtoList(List<Moment> moments);

    List<Moment> toEntityList(List<MomentDto> dtos);

    @Named("projectsToIds")
    default List<Long> mapProjectsToIds(List<Project> projects) {
        if (projects == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(projects.stream()
                .map(Project::getId)
                .collect(Collectors.toList()));
    }

    @Named("idsToProjects")
    default List<Project> mapIdsToProjects(List<Long> ids) {
        if (ids == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(ids.stream()
                .map(id -> {
                    Project project = new Project();
                    project.setId(id);
                    return project;
                })
                .collect(Collectors.toList()));
    }

    @Named("resourcesToIds")
    default List<Long> mapResourcesToIds(List<Resource> resources) {
        if (resources == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(resources.stream()
                .map(Resource::getId)
                .collect(Collectors.toList()));
    }

    @Named("idsToResources")
    default List<Resource> mapIdsToResources(List<Long> ids) {
        if (ids == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(ids.stream()
                .map(id -> {
                    Resource resource = new Resource();
                    resource.setId(id);
                    return resource;
                })
                .collect(Collectors.toList()));
    }
}


