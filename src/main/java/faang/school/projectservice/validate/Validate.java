package faang.school.projectservice.validate;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.model.stage.StageStatus;

import java.util.Arrays;

public class Validate {
    public static void validateId(StageDto stageDto) {
        if (stageDto.getStageId() != null) {
            throw new IllegalArgumentException("Stage ID must be null");
        }
        if(stageDto.getStageRolesDto().stream().anyMatch(stageRolesDto -> stageRolesDto.getId() != null)) {
            throw new IllegalArgumentException("Stage roles ID must be null");
        }
    }

    public static void validateStatus(String status) {
        if (Arrays.stream(StageStatus.values()).noneMatch(stageStatus -> stageStatus.toString().equalsIgnoreCase(status))) {
            throw new IllegalArgumentException("Invalid status");
        }
    }

    public static void validateStageId(Long stageId) {
        if (stageId == null) {
            throw new IllegalArgumentException("Stage ID must not be null");
        }
    }
}
