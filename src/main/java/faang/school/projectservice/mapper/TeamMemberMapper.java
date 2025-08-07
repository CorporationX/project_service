package faang.school.projectservice.mapper;

import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TeamMemberMapper {

    TeamMemberDto toDto(TeamMember member);

    List<TeamMemberDto> toDtoList(List<TeamMember> members);
}
