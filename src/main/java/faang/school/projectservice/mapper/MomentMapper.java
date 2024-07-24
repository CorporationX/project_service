package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {
    @Mapping(target = "projects", source = "projectIds", qualifiedByName = "fromProjectIdsToProjects")
    @Mapping(target = "members", source = "memberIds", qualifiedByName = "fromMemberIdsToMembers")
    Moment toEntity(MomentDto momentDto);

    @Mapping(target = "projectIds", source = "projects", qualifiedByName = "fromProjectsToProjectIds")
    @Mapping(target = "memberIds", source = "members", qualifiedByName = "fromMembersToMemberIds")
    MomentDto toDto(Moment moment);

    void update (MomentDto momentDto, @MappingTarget Moment moment);

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

    @Named("fromMemberIdsToMembers")
    default List<TeamMember> toMembers(List<Long> memberIds) {
        if (memberIds.isEmpty()) {
            return Collections.emptyList();
        } else {
            return memberIds.stream().map(id -> {
                TeamMember teamMember = new TeamMember();
                teamMember.setId(id);
                return teamMember;
            }).collect(Collectors.toList());
        }
    }

    @Named("fromMembersToMemberIds")
    default List<Long> toMemberIds(List<TeamMember> members) {
        if (members.isEmpty()) {
            return Collections.emptyList();
        } else {
            return members.stream().map(TeamMember::getId).collect(Collectors.toList());
        }
    }
}
