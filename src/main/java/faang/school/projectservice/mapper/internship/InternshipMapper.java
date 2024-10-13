package faang.school.projectservice.mapper.internship;

import faang.school.projectservice.model.dto.InternshipDto;
import faang.school.projectservice.model.entity.Internship;
import faang.school.projectservice.model.entity.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InternshipMapper {

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "mentor.userId", target = "mentorUserId")
    @Mapping(source = "createdBy", target = "creatorUserId")
    @Mapping(source = "interns", target = "internUserIds", qualifiedByName = "membersToUserIds")
    InternshipDto toDto(Internship internship);

    List<InternshipDto> toDtoList(List<Internship> internships);

    @Named("membersToUserIds")
    default List<Long> mapTeamMembersToUserIds(List<TeamMember> teamMembers) {
        if (teamMembers == null) {
            return null;
        }
        return teamMembers.stream()
                .map(TeamMember::getUserId)
                .collect(Collectors.toList());
    }
}

