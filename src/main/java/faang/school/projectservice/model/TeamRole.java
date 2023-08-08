package faang.school.projectservice.model;

import java.util.List;

public enum TeamRole {
    OWNER,
    MANAGER,
    DEVELOPER,
    DESIGNER,
    TESTER,
    ANALYST,
    INTERN_MANAGER,
    INTERN_DEVELOPER,
    INTERN_DESIGNER,
    INTERN_TESTER,
    INTERN_ANALYST;

    public static List<TeamRole> getAll() {
        return List.of(TeamRole.values());
    }
}