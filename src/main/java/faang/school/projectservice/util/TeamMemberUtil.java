package faang.school.projectservice.util;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;

import java.util.List;

public class TeamMemberUtil {
    private static final String NO_OWNER_PRIVILEGES = "Caller doesn't have project owner privileges";
    private static final String NO_TEAM_LEAD_PRIVILEGES = "Caller doesn't have project owner privileges";
    private static final String NO_TEAM_LEAD_OR_OWNER_PRIVILEGES = "Caller doesn't have project owner or team lead privileges";
    private static final String NO_TEAM_MEMBER_OWNER_PRIVILEGES = "Calling user doesn't own team member account";
    private static final String ROLES_EMPTY_OR_CONTAINS_NULLS = "Team member must have at least a single role, and no roles can be null";
    public static void validateNewRolesNotEmpty(List<TeamRole> newRoles) {
        if (newRoles.isEmpty() || newRoles.contains(null)) {
            throw new IllegalArgumentException(ROLES_EMPTY_OR_CONTAINS_NULLS);
        }
    }

    public static void validateProjectOwner(Project target, Long callerId) {
        if (!isProjectOwner(target, callerId)) {
            throw new SecurityException(NO_OWNER_PRIVILEGES);
        }
    }

    public static void validateTeamLead(TeamMember caller) {
        if (!isTeamLead(caller)) {
            throw new SecurityException(NO_TEAM_LEAD_PRIVILEGES);
        }
    }

    public static void validateOwnerOrTeamLead(Project target, Long callerId, TeamMember caller) {
        boolean eitherOwnerOrTeamLead = isProjectOwner(target, callerId) || isTeamLead(caller);
        if (!eitherOwnerOrTeamLead) {
            throw new SecurityException(NO_TEAM_LEAD_OR_OWNER_PRIVILEGES);
        }
    }

    public static void validateCallerOwnsAccount(TeamMember member, Long userId) {
        if (!member.getUserId().equals(userId)) {
            throw new SecurityException(NO_TEAM_MEMBER_OWNER_PRIVILEGES);
        }
    }

    private static boolean isTeamLead(TeamMember caller) {
        return caller.getRoles().contains(TeamRole.TEAM_LEAD);
    }

    private static boolean isProjectOwner(Project target, Long callerId) {
        return target.getOwnerId().equals(callerId);
    }
}
