package faang.school.projectservice.validator.teammember;

import faang.school.projectservice.model.TeamMember;

public interface TeamMemberValidator {
    TeamMember validateTeamMemberExistence(long id);
}
