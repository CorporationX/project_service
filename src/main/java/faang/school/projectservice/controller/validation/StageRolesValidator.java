package faang.school.projectservice.controller.validation;

import faang.school.projectservice.model.TeamRole;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class StageRolesValidator {
    private final TeamRoleValidator validateIsItTeamRole;

    public void validateRolesWithAmount(Map<TeamRole, Integer> rolesWithAmount) {
        if (rolesWithAmount == null || rolesWithAmount.isEmpty()) {
            throw new IllegalArgumentException("Roles for the stage must be specified");
        }
        rolesWithAmount.entrySet()
                .forEach(entry -> {
                    if (entry.getKey() == null
                            || entry.getValue() == null
                            || entry.getValue() <= 0) {
                        throw new IllegalArgumentException("Invalid role or amount: " + entry);
                    }
                    validateIsItTeamRole.validateIsItTeamRole(entry.getKey());
                });

    }
}
