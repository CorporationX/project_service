package faang.school.projectservice.controller.validation;

import faang.school.projectservice.dto.project.StageDto;
import org.springframework.stereotype.Component;

@Component
public class StageValidator {
    public static void validateCreation(StageDto stageDto) {
        if (stageDto.stageName().isEmpty()) {
            throw new ConstraintViolation("Stage name cannot be empty");
        }
    }
}
