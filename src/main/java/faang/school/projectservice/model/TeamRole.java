package faang.school.projectservice.model;

import java.util.List;

public enum TeamRole {
    OWNER,
    MANAGER,
    DEVELOPER,
    DESIGNER,
    TESTER,
    ANALYST,
    INTERNMANAGER,
    INTERNDEVELOPER,
    INTERNDESIGNER,
    INTERNTESTER,
    INTERNANALYST;

    public static List<TeamRole> getAll() {
        return List.of(TeamRole.values());
    }
}