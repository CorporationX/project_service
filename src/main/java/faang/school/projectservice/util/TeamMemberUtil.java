package faang.school.projectservice.util;

import faang.school.projectservice.model.Project;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;

import java.util.List;
import java.util.Objects;

public class TeamMemberUtil {
    private static final String NO_OWNER_PRIVILEGES = "Caller doesn't have project owner privileges";
    private static final String NO_TEAM_LEAD_PRIVILEGES = "Caller doesn't have team lead privileges";
    private static final String NO_TEAM_MEMBER_OWNER_PRIVILEGES = "Calling user doesn't own team member account";

    private static final String NOT_TEAM_LEAD_NOR_OWNER_PRIVILEGES = "Caller doesn't have team lead or project owner privileges";
    private static final String ROLES_EMPTY_OR_CONTAINS_NULLS = "Team member must have at least a single role, and no roles can be null";

    public static void validateNewRolesNotEmpty(List<TeamRole> newRoles) {
        if (newRoles.isEmpty()) {
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

    public static void validateProjectOwnerOrTeamLead(TeamMember caller, Long callerId, Project teamProject) {
        if (Objects.isNull(caller)) {
            validateProjectOwner(teamProject, callerId);
        } else {
            boolean ownerOrTeamLead = isProjectOwner(teamProject, callerId) || isTeamLead(caller);
            if (!ownerOrTeamLead) {
                throw new SecurityException(NOT_TEAM_LEAD_NOR_OWNER_PRIVILEGES);
            }
        }
    }

    public static void validateCallerOwnsAccount(TeamMember member, Long userId) {
        if (!member.getUserId().equals(userId)) {
            throw new SecurityException(NO_TEAM_MEMBER_OWNER_PRIVILEGES);
        }
    }

    private static boolean isTeamLead(TeamMember caller) {
        return Objects.nonNull(caller) && caller.getRoles().contains(TeamRole.TEAM_LEAD);
    }

    public static boolean isProjectOwner(Project target, Long callerId) {
        return target.getOwnerId().equals(callerId);
    }

    public static void validateUserNotInProject(TeamMember member, Long projectId) {
        if (!Objects.isNull(member)) {
            throw new IllegalStateException(
                    String.format("User with provided ID is already a member in this project (ID %s)", projectId));
        }
    }
}