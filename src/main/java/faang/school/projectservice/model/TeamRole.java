package faang.school.projectservice.model;

import java.util.List;

public enum TeamRole {
    OWNER,
    MANAGER,
    TEAM_LEAD,
    DEVELOPER,
    DESIGNER,
    TESTER,
    ANALYST,
    INTERN;

    public static List<TeamRole> getAll() {
        return List.of(TeamRole.values());
    }
}