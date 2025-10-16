package faang.school.projectservice.dto.stage;

import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.StageRoles;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import static faang.school.projectservice.dto.stage.StageConstraints.MIN_STAGE_ROLE_COUNT;

/**
 * StageRoleDto — dto для {@link StageRoles}.
 *
 * @param role роль из перечисления {@link TeamRole}
 * @param count количество человек с данной ролью
 * @author bozya
 * @since 01.08.2025
 */
public record StageRolesDto(
        @NotNull
        TeamRole role,
        @Min(MIN_STAGE_ROLE_COUNT)
        Integer count
) {
}