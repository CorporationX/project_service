package faang.school.projectservice.validator.stagerole;

import faang.school.projectservice.model.stage.StageRoles;

public interface StageRolesValidator {
    StageRoles validateStageRolesExistence(long stageRoleId);
}
