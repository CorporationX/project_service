package faang.school.projectservice.validator;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.dto.StageDtoForUpdate;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageStatus;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;

@Component
public class StageValidator {
    public static void validateId(StageDto stageDto) {
        if (stageDto == null) {
            throw new DataValidationException("Stage must not be null");
        }
        if (stageDto.getStageRolesDto().stream().anyMatch(Objects::isNull)) {
            throw new DataValidationException("Stage roles must not be null");
        }
    }

    public static void validateStatus(String status) {
        if (Arrays.stream(StageStatus.values()).noneMatch(stageStatus -> stageStatus.toString().equalsIgnoreCase(status))) {
            throw new DataValidationException("Invalid status");
        }
    }

    public static void validateStageId(Long stageId) {
        if (stageId == null) {
            throw new DataValidationException("Stage ID must not be null");
        }
    }

    public static void validateStageDto(StageDtoForUpdate stageDto) {
        if (stageDto == null) {
            throw new DataValidationException("Stage must not be null");
        }
        validateStageId(stageDto.getStageId());
        if (stageDto.getTeamRoles() == null
                || stageDto.getTeamRoles().size() == 0) {
            throw new DataValidationException("List team roles must not be null or empty");
        }
    }

    public void isCompletedOrCancelled(Stage stageFromRepository) {
        if (stageFromRepository.getProject().getStatus().toString().equalsIgnoreCase("CANCELLED")
                || stageFromRepository.getProject().getStatus().toString().equalsIgnoreCase("COMPLETED")) {
            throw new DataValidationException("Project is completed or cancelled");
        }
        if (stageFromRepository.getStatus().toString().equals("CANCELLED")
                || stageFromRepository.getStatus().toString().equals("COMPLETED")) {
            throw new DataValidationException("Stage is completed or cancelled");
        }
    }
}