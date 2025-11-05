package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.client.TeamMemberDto;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMemberMapper {
    TeamMember toTeamMember(TeamMemberDto teamMemberDto);

    TeamMemberDto toTeamMemberDto(TeamMember teamMember);
}
