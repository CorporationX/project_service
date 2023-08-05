package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Resource;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {

    @Mapping(source = "resource", target = "resourceIds", qualifiedByName = "resourceToIdList")
    @Mapping(source = "projects", target = "projectIds", qualifiedByName = "projectsToIdList")
    MomentDto toDto(Moment moment);

    @Mapping(source = "projectIds", target = "projects", qualifiedByName = "idsToProjectList")
    @Mapping(source = "resourceIds", target = "resource", qualifiedByName = "idsToResourceList")
    Moment toEntity(MomentDto momentDto);

    @Named("resourceToIdList")
    default List<Long> resourceToIdList(List<Resource> resource) {
        return resource != null ? resource.stream().map(Resource::getId).toList() : Collections.emptyList();
    }

    @Named("projectsToIdList")
    default List<Long> projectsToIdList(List<Project> projects) {
        return projects != null ? projects.stream().map(Project::getId).toList() : Collections.emptyList();
    }

    @Named("idsToProjectList")
    default List<Project> idsToProjectList(List<Long> projectIds) {
        return projectIds != null ? projectIds.stream()
                .map(projectId -> {
                    Project project = new Project();
                    project.setId(projectId);
                    return project;
                }).toList() : Collections.emptyList();
    }

    @Named("idsToResourceList")
    default List<Resource> idsToResourceList(List<Long> resourceIds) {
        return resourceIds != null ? resourceIds.stream()
                .map(resourceId -> {
                    Resource resource = new Resource();
                    resource.setId(resourceId);
                    return resource;
                }).toList() : Collections.emptyList();
    }
}
