package faang.school.projectservice.mapper.moment;

import faang.school.projectservice.dto.moment.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.Team;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.*;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        injectionStrategy = InjectionStrategy.FIELD)
public interface MomentMapper {
    @Mapping(target = "projects", source = "projectIds", qualifiedByName = "idsToProjectList")
    Moment toMoment(MomentDto momentDto);

    @Mapping(target = "projectIds", source = "projects", qualifiedByName = "projectsToIdList")
    MomentDto toMomentDto(Moment moment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "projectIds", source = "id", qualifiedByName = "idToList")
    @Mapping(target = "createdBy", source = "ownerId")
    @Mapping(target = "name", constant = "Выполнены все подпроекты")
    @Mapping(target = "userIds", source = "teams", qualifiedByName = "teamsUserToIdList")
    MomentDto toMomentDto(Project project);

    @Named("teamsUserToIdList")
    default List<Long> teamsUserToIdList(List<Team> teams) {
        return teams != null ? teams.stream()
                .flatMap(team -> team.getTeamMembers().stream())
                .map(TeamMember::getUserId)
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
}
