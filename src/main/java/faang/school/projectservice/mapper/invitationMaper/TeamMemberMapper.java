package faang.school.projectservice.mapper.invitationMaper;

import faang.school.projectservice.dto.invitation.DtoTeamMember;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMemberMapper {
    TeamMemberMapper INSTANCE = Mappers.getMapper(TeamMemberMapper.class);

    TeamMember toTeamMember(Long id);

    DtoTeamMember toDtoTeamMember(TeamMember teamMember);
}
