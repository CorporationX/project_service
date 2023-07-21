package faang.school.projectservice.model;

import java.util.List;

public enum TeamRole {
    OWNER,
    MANAGER,
    DEVELOPER,
    DESIGNER,
    TESTER,
    ANALYST;

    public static List<TeamRole> getAll() {
        return List.of(TeamRole.values());
    }
}