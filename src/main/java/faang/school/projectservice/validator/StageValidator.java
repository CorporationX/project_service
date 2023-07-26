package faang.school.projectservice.validator;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageStatus;

import java.util.Arrays;

public class StageValidator {
    public static void validateId(StageDto stageDto) {
        if (stageDto.getStageId() != null) {
            throw new IllegalArgumentException("Stage ID must be null");
        }
        if (stageDto.getStageRolesDto().stream().anyMatch(stageRolesDto -> stageRolesDto.getId() != null)) {
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

    public static void validateStageDto(StageDto stageDto) {
        validateStageId(stageDto.getStageId());
        if (stageDto.getStageRolesDto().size() == 0) {
            throw new IllegalArgumentException("List stage roles must not be empty");
        }
        if (stageDto.getStageRolesDto().stream().anyMatch(stageRolesDto -> (
                stageRolesDto.getTeamRole() == null)
                || stageRolesDto.getTeamRole().isEmpty()
                || stageRolesDto.getTeamRole().isBlank())) {
            throw new IllegalArgumentException("Team role must not be null");
        }
    }

    public static void isCompletedOrCancelled(Stage stageFromRepository) {
        if (stageFromRepository.getProject().getStatus().toString().equalsIgnoreCase("CANCELLED")
        || stageFromRepository.getProject().getStatus().toString().equalsIgnoreCase("COMPLETED")) {
            throw new IllegalArgumentException("Project is completed or cancelled");
        }
        if (stageFromRepository.getStatus().toString().equalsIgnoreCase("CANCELLED")
        || stageFromRepository.getStatus().toString().equalsIgnoreCase("COMPLETED")) {
            throw new IllegalArgumentException("Stage is completed or cancelled");
        }
    }
}