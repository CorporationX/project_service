package faang.school.projectservice.service;

import faang.school.projectservice.dto.team.TeamFilterDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TeamMemberService {

    TeamMemberDto addTeamMember(long id, TeamMemberDto teamMemberDto);

    TeamMemberDto updateTeamMember(long id, TeamMemberDto teamMemberDto);

    void deleteTeamMember(long teamId, long userId);

    List<TeamMemberDto> getTeamMembersByFilter(long id, TeamFilterDto filters);

    Page<TeamMemberDto> getAllTeamMembers(Pageable pageable);

    TeamMemberDto getTeamMemberById(long teamMemberId);
}
