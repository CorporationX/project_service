package faang.school.projectservice.mapper.team;

import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = org.mapstruct.ReportingPolicy.IGNORE)
public interface TeamMemberMapper {
    TeamMemberDto toDto(TeamMember teamMember);

    TeamMember toEntity(TeamMemberDto teamMemberDto);
}
