package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.MomentDto;
import faang.school.projectservice.model.Moment;
import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedSourcePolicy = ReportingPolicy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MomentMapper {
    @Mapping(source = "projects", target = "projectIds", qualifiedByName = "toProjectIds")
    @Mapping(source = "teamMembers", target = "teamMemberIds", qualifiedByName = "toTeamMemberIds")
    MomentDto toDto(Moment moment);
    @Mapping(source = "projectIds", target = "projects", qualifiedByName = "toProjects")
    Moment toEntity(MomentDto momentDto);

    @Named("toProjectIds")
    default List<Long> projectsToIds(List<Project> projects) {
        return projects.stream().map(Project::getId).toList();
    }

    @Named("toProjects")
    default List<Project> idsToProjects(List<Long> ids) {
        return ids.stream().map(id -> Project.builder().id(id).build()).toList();
    }

    @Named("toTeamMemberIds")
    default List<Long> teamMembersToIds(List<TeamMember> teamMembers) {
        return teamMembers.stream().map(TeamMember::getId).toList();
    }
}
