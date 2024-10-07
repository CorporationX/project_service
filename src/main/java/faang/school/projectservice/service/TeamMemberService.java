package faang.school.projectservice.service;

import faang.school.projectservice.dto.team.TeamFilterDto;
import faang.school.projectservice.dto.team.TeamMemberCreateDto;
import faang.school.projectservice.dto.team.TeamMemberDto;
import faang.school.projectservice.dto.team.TeamMemberUpdateDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface TeamMemberService {

    TeamMemberDto addTeamMember(long teamId, TeamMemberCreateDto teamMemberCreateDto);

    TeamMemberDto updateTeamMember(long teamId, long teamMemberId, TeamMemberUpdateDto teamMemberUpdateDto);

    void deleteTeamMember(long teamMemberId);

    List<TeamMemberDto> getTeamMembersByFilter(long teamId, TeamFilterDto filters);

    Page<TeamMemberDto> getAllTeamMembers(Pageable pageable);

    TeamMemberDto getTeamMemberById(long teamMemberId);
}
