package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.*;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD)
public interface MomentMapper {
    @Mapping(target = "projects", source = "projectIds", qualifiedByName = "idsToProjectList")
    @Mapping(target = "resource", source = "resourceIds", qualifiedByName = "IdsToResources")
    @Mapping(target = "members", source = "memberIds", qualifiedByName = "IdsToMembers")
    Moment toMoment(MomentDto momentDto);

    @Mapping(target = "projectIds", source = "projects", qualifiedByName = "projectsToIdList")
    @Mapping(target = "resourceIds", source = "resource", qualifiedByName = "ResourcesToIds")
    @Mapping(target = "memberIds", source = "members", qualifiedByName = "MembersToIds")
    MomentDto toMomentDto(Moment moment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "projectIds", source = "id", qualifiedByName = "idToList")
    @Mapping(target = "createdBy", source = "ownerId")
    @Mapping(target = "name", constant = "Выполнены все подпроекты")
    @Mapping(target = "memberIds", source = "teams", qualifiedByName = "teamsUserToIdList")
    MomentDto toMomentDto(Project project);

    @Named("teamsUserToIdList")
    default List<Long> teamsUserToIdList(List<Team> teams) {
        return teams != null ? teams.stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getId)
                .collect(Collectors.toList()) : Collections.emptyList();
    }

    @Named("projectsToIdList")
    default List<Long> projectsToIdList(List<Project> projects) {
        return projects != null ? projects.stream()
                .map(Project::getId)
                .toList() : Collections.emptyList();
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

    @Named("idToList")
    default List<Long> idToList(Long id) {
        return Stream.of(id).toList();
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

    @Named("IdsToMembers")
    default List<TeamMember> memberIdsToMembers(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return new ArrayList<>();
        }

        return ids.stream()
                .map(id -> TeamMember.builder().id(id).build())
                .toList();
    }

    @Named("MembersToIds")
    default List<Long> MembersToMemberIds(List<TeamMember> members) {
        if (members == null || members.isEmpty()) {
            return new ArrayList<>();
        }

        return members.stream()
                .map(TeamMember::getId)
                .toList();
    }
}
