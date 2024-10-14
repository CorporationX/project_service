package faang.school.projectservice.mapper.teammember;

import faang.school.projectservice.dto.teammember.AddTeamMemberDto;
import faang.school.projectservice.dto.teammember.TeamMemberDto;
import faang.school.projectservice.dto.teammember.UpdateTeamMemberDto;
import faang.school.projectservice.model.TeamMember;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TeamMemberMapper {

    TeamMemberDto toDto(TeamMember teamMember);
    
    TeamMemberDto toDto(UpdateTeamMemberDto teamMember);
    
    TeamMember toEntity(TeamMemberDto teamMemberDto);

    TeamMember toEntity(AddTeamMemberDto addTeamMemberDto);

    List<TeamMemberDto> toDtos(List<TeamMember> teamMembers);
}
