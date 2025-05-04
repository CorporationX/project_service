package faang.school.projectservice.service.teammember;

import faang.school.projectservice.model.TeamMember;

import java.nio.file.AccessDeniedException;

public interface TeamMemberService {

    TeamMember getCurrentTeamMember(long projectId) throws AccessDeniedException;
}
