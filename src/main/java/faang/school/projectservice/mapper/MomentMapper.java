package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {
    @Mapping(target = "projects", source = "projectIds", qualifiedByName = "fromProjectIdsToProjects")
    Moment toEntity(MomentDto momentDto);

    @Mapping(target = "projectIds", source = "projects", qualifiedByName = "fromProjectsToProjectIds")
    MomentDto toDto(Moment moment);

    @Named("fromProjectIdsToProjects")
    default List<Project> toProjects(List<Long> projectIds) {
        if (projectIds.isEmpty()) {
            return Collections.emptyList();
        } else {
            return projectIds.stream().map(id -> {
                Project project = new Project();
                project.setId(id);
                return project;
            }).collect(Collectors.toList());
        }
    }

    @Named("fromProjectsToProjectIds")
    default List<Long> toProjectIds(List<Project> projects) {
        if (projects.isEmpty()) {
            return Collections.emptyList();
        } else {
            return projects.stream().map(Project::getId).collect(Collectors.toList());
        }
    }
}
