package faang.school.projectservice.service;

import faang.school.projectservice.dto.team.TeamFilterDto;
import faang.school.projectservice.dto.team.TeamMemberDto;

import java.util.List;

public interface TeamMemberService {

    TeamMemberDto addTeamMember(long id, TeamMemberDto teamMemberDto);

    TeamMemberDto updateTeamMember(long id, TeamMemberDto teamMemberDto);

    void deleteTeamMember(long id);

    List<TeamMemberDto> getTeamMembersByFilter(TeamFilterDto filters);

    List<TeamMemberDto> getAllTeamMembers();

    TeamMemberDto getTeamMemberById(long teamMemberId);
}
