package faang.school.projectservice.validator;

import faang.school.projectservice.exception.DataValidationException;
import faang.school.projectservice.model.ProjectStatus;
import faang.school.projectservice.model.stage.Stage;
import faang.school.projectservice.model.stage.StageStatus;
import org.springframework.stereotype.Component;

@Component
public class StageValidator {

    public void isCompletedOrCancelled(Stage stageFromRepository) {
        if (stageFromRepository.getStatus().equals(StageStatus.CANCELLED)
                || stageFromRepository.getStatus().equals(StageStatus.COMPLETED)) {
            throw new DataValidationException("Stage is completed or cancelled");
        }
        if (stageFromRepository.getProject().getStatus().equals(ProjectStatus.CANCELLED)
                || stageFromRepository.getProject().getStatus().equals(ProjectStatus.COMPLETED)) {
            throw new DataValidationException("Project is completed or cancelled");
        }
    }
}