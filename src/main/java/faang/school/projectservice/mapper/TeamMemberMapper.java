package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.team_member.TeamMemberDto;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "Spring", unmappedSourcePolicy = ReportingPolicy.IGNORE)
public interface TeamMemberMapper {

    TeamMemberDto toDto(TeamMember teamMember);

    TeamMember toEntity(TeamMemberDto teamMemberDto);
}
