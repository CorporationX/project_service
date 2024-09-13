package faang.school.projectservice.controller.validation;

import faang.school.projectservice.dto.stage.StageDto;
import faang.school.projectservice.model.TeamRole;
import org.springframework.stereotype.Component;

@Component
public class StageValidator {
    public static void validateCreation(StageDto stageDto) {
        if (stageDto.stageName() == null || stageDto.stageName().isEmpty()) {
            throw new IllegalArgumentException("Stage name cannot be empty");
        }
        if (stageDto.projectId() == null) {
            throw new IllegalArgumentException("The stage must be related to the project");
        }
        if (stageDto.rolesWithAmount() == null || stageDto.rolesWithAmount().isEmpty()) {
            throw new IllegalArgumentException("Roles for the stage must be specified");
        }
        stageDto.rolesWithAmount().entrySet()
                .forEach(entry -> {
                    if (entry.getKey() == null
                            || !TeamRole.getAll().contains(entry.getKey())
                            || entry.getValue() == null
                            || entry.getValue() <= 0) {
                        throw new IllegalArgumentException("Invalid role or amount: " + entry);
                    }
                });
    }
}
