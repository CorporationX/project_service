package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {
    @Mapping(target = "projects", source = "projectIds", qualifiedByName = "fromProjectIdsToProjects")
    @Mapping(target = "userIds", source = "userIds", qualifiedByName = "toUserIds")
    Moment toEntity(MomentDto momentDto);

    @Mapping(target = "projectIds", source = "projects", qualifiedByName = "fromProjectsToProjectIds")
    @Mapping(target = "userIds", source = "userIds", qualifiedByName = "toUserIds")
    MomentDto toDto(Moment moment);

    void update (MomentDto momentDto, @MappingTarget Moment moment);

    @Named("fromProjectIdsToProjects")
    default List<Project> toProjects(List<Long> projectIds) {
        if (projectIds == null || projectIds.isEmpty()) {
            return new ArrayList<>();
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
        if (projects == null || projects.isEmpty()) {
            return new ArrayList<>();
        } else {
            return projects.stream().map(Project::getId).collect(Collectors.toList());
        }
    }

    @Named("toUserIds")
    default List<Long> toUserIds(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return new ArrayList<>();
        } else {return userIds;}
    }
}
