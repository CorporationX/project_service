package faang.school.projectservice.service.stage;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.TeamMember;
import faang.school.projectservice.model.TeamRole;
import faang.school.projectservice.model.stage.StageRoles;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class StageServiceValidator {

    public List<DataValidationException> getUnnecessaryExecutorsExist(List<TeamMember> members, List<StageRoles> roles) {
        List<DataValidationException> errors = new ArrayList<>();
        Map<TeamRole, Integer> rolesCount = new HashMap<>();
        roles
                .forEach(stageRole ->
                        rolesCount.put(stageRole.getTeamRole(), stageRole.getCount()));

        members.stream()
                .flatMap(teamMember -> teamMember.getRoles().stream())
                .forEach(role -> {
                    if (rolesCount.containsKey(role)) {
                        int count = rolesCount.get(role);
                        if (count == 0) {
                            errors.add(new DataValidationException("Unnecessary role: " + role));
                        }
                        rolesCount.put(role, count - 1);
                    } else {
                        errors.add(new DataValidationException("Unnecessary role: " + role));
                    }
                });
        return errors;
    }
}
