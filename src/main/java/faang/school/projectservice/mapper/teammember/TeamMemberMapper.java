package faang.school.projectservice.mapper.teammember;

import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.model.TeamMember;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMemberMapper {

    TeamMemberDto toDto(TeamMember teamMember);

    TeamMember toEntity(TeamMemberDto teamMemberDto);
}
