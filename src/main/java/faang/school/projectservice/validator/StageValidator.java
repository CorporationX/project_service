package faang.school.projectservice.validator;

import faang.school.projectservice.dto.StageDto;
import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.ProjectStatus;
import org.springframework.stereotype.Component;

@Component
public class StageValidator {

    public void validateStage(StageDto stageDto) {
        if (stageDto == null) {
            throw new DataValidationException("Stage cannot be null");
        }
        if (stageDto.getProject() == null) {
            throw new DataValidationException("The stage NECESSARILY refers to some kind of project!!!");
        }
        if (stageDto.getStageName() == null || stageDto.getStageName().isBlank()) {
            throw new DataValidationException("Stage name cannot be null or blank");
        }
        if (stageDto.getStageRoles() == null || stageDto.getStageRoles().isEmpty()) {
            throw new DataValidationException("Stage roles cannot be null or empty");
        }
    }

    public void validateStageDtoForProjectCompletedAndCancelled(StageDto stageDto) {
        if (stageDto.getProject().getStatus().equals(ProjectStatus.COMPLETED) ||
                stageDto.getProject().getStatus().equals(ProjectStatus.CANCELLED)) {
            throw new DataValidationException("You cannot create a stage in a closed or canceled project");
        }
    }
}
