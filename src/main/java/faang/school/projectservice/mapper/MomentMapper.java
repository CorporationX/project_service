package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    @Mapping(target = "projects", source = "projectIds", qualifiedByName = "IdsToProjects")
    @Mapping(target = "resource", source = "resourceIds", qualifiedByName = "IdsToResources")
    Moment toEntity(MomentDto momentDto);

    @Mapping(target = "projectIds", source = "projects", qualifiedByName = "ProjectsToIds")
    @Mapping(target = "resourceIds", source = "resource", qualifiedByName = "ResourcesToIds")
    MomentDto toDto(Moment moment);

    @Named("IdsToProjects")
    default List<Project> projectIdsToProjects(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        return ids.stream()
                .map(id -> Project.builder().id(id).build())
                .toList();
    }

    @Named("ProjectsToIds")
    default List<Long> projectsToProjectIds(List<Project> projects) {
        if (projects == null || projects.isEmpty()) {
            return new ArrayList<>();
        }

        return projects.stream()
                .map(Project::getId)
                .toList();
    }

    @Named("IdsToResources")
    default List<Resource> resourceIdsToResources(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        return ids.stream()
                .map(id -> Resource.builder().id(id).build())
                .toList();
    }

    @Named("ResourcesToIds")
    default List<Long> resourcesToResourceIds(List<Resource> resources) {
        if (resources == null || resources.isEmpty()) {
            return new ArrayList<>();
        }

        return resources.stream()
                .map(Resource::getId)
                .toList();
    }
}
