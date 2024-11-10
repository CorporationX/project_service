package faang.school.projectservice.exception;

import faang.school.projectservice.model.TeamRole;

public class NotEnoughTeamMembersException extends StageServiceException {
    public NotEnoughTeamMembersException(TeamRole teamRole, Long projectId) {
        super("Not enough team members with role " + teamRole + " in project " + projectId);
    }
}