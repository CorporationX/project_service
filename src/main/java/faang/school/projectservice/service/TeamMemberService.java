package faang.school.projectservice.service;

import faang.school.projectservice.dto.client.TeamMemberDto;

public interface TeamMemberService {

    TeamMemberDto getTeamMember(long userId, long projectId);
}
